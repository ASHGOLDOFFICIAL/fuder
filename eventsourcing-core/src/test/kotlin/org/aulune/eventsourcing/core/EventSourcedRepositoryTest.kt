package org.aulune.eventsourcing.core

import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.eventsourcing.core.memory.InMemoryEventStore
import org.aulune.eventsourcing.core.memory.InMemorySnapshotStore
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

private sealed interface CounterEvent {
    data class Incremented(
        val amount: Int,
    ) : CounterEvent

    data class Decremented(
        val amount: Int,
    ) : CounterEvent
}

private sealed interface CounterCommand {
    data class Increment(
        val amount: Int,
    ) : CounterCommand

    data class Decrement(
        val amount: Int,
    ) : CounterCommand
}

private data object WouldGoNegative

private object CounterDecider : Decider<Int, CounterCommand, CounterEvent, WouldGoNegative> {
    override val initial = 0

    override fun decide(
        state: Int,
        command: CounterCommand,
    ) = either {
        when (command) {
            is CounterCommand.Increment -> {
                listOf(CounterEvent.Incremented(command.amount))
            }

            is CounterCommand.Decrement -> {
                ensure(state - command.amount >= 0) { WouldGoNegative }
                listOf(CounterEvent.Decremented(command.amount))
            }
        }
    }

    override fun evolve(
        state: Int,
        event: CounterEvent,
    ): Int = when (event) {
        is CounterEvent.Incremented -> state + event.amount
        is CounterEvent.Decremented -> state - event.amount
    }
}

private class RecordingEventStore<E>(
    private val delegate: EventStore<E>,
) : EventStore<E> by delegate {
    val readCalls = mutableListOf<StreamVersion>()

    override fun read(
        streamId: StreamId,
        afterVersion: StreamVersion,
    ): List<StoredEvent<E>> {
        readCalls.add(afterVersion)
        return delegate.read(streamId, afterVersion)
    }
}

class EventSourcedRepositoryTest {

    private val streamId = StreamId("counter-1")

    private fun repository(
        eventStore: EventStore<CounterEvent> = InMemoryEventStore(),
        snapshotStore: SnapshotStore<Int> = InMemorySnapshotStore(),
        schemaVersion: SchemaVersion = SchemaVersion(1),
        snapshotFrequency: Int = 100,
    ) = EventSourcedRepository(CounterDecider, eventStore, snapshotStore, schemaVersion, snapshotFrequency)

    @Nested
    inner class `given several events across multiple commands` {

        @Nested
        inner class `when loading` {

            @Test
            fun `then state reflects all of them in order`() {
                val repo = repository()

                repo.handle(streamId, CounterCommand.Increment(5))
                repo.handle(streamId, CounterCommand.Decrement(2))
                repo.handle(streamId, CounterCommand.Increment(10))

                val loaded = repo.load(streamId)

                assertEquals(13, loaded.state)
                assertEquals(
                    StreamVersion.INITIAL
                        .next()
                        .next()
                        .next(),
                    loaded.version,
                )
            }
        }
    }

    @Nested
    inner class `given a command the decider rejects` {

        @Nested
        inner class `when handling it` {

            @Test
            fun `then nothing is appended`() {
                val repo = repository()
                repo.handle(streamId, CounterCommand.Increment(1))

                val result = repo.handle(streamId, CounterCommand.Decrement(5))

                assertEquals(HandleError.Rejected(WouldGoNegative), result.leftOrNull())
                assertEquals(1, repo.load(streamId).state)
            }
        }
    }

    @Nested
    inner class `given events crossing the snapshot frequency` {

        @Nested
        inner class `when loading again` {

            @Test
            fun `then only events after the snapshot are read`() {
                val eventStore = RecordingEventStore(InMemoryEventStore<CounterEvent>())
                val repo = repository(eventStore = eventStore, snapshotFrequency = 2)

                repo.handle(streamId, CounterCommand.Increment(1))
                repo.handle(streamId, CounterCommand.Increment(1))
                eventStore.readCalls.clear()

                val loaded = repo.load(streamId)

                assertEquals(2, loaded.state)
                assertEquals(listOf(StreamVersion.INITIAL.next().next()), eventStore.readCalls)
            }
        }
    }

    @Nested
    inner class `given a snapshot from a different schema version` {

        @Nested
        inner class `when loading` {

            @Test
            fun `then the stream is replayed from the start`() {
                val eventStore = RecordingEventStore(InMemoryEventStore<CounterEvent>())
                val snapshotStore = InMemorySnapshotStore<Int>()
                repository(eventStore, snapshotStore, schemaVersion = SchemaVersion(1), snapshotFrequency = 1)
                    .handle(streamId, CounterCommand.Increment(5))
                val repoV2 = repository(eventStore, snapshotStore, schemaVersion = SchemaVersion(2), snapshotFrequency = 1)
                eventStore.readCalls.clear()

                val loaded = repoV2.load(streamId)

                assertEquals(5, loaded.state)
                assertEquals(listOf(StreamVersion.INITIAL), eventStore.readCalls)
            }
        }
    }
}
