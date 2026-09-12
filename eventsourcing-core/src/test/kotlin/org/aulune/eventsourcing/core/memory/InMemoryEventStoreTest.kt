package org.aulune.eventsourcing.core.memory

import arrow.core.nonEmptyListOf
import org.aulune.eventsourcing.core.ConcurrencyConflict
import org.aulune.eventsourcing.core.StreamId
import org.aulune.eventsourcing.core.StreamVersion
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryEventStoreTest {

    private val streamId = StreamId("test-1")

    @Nested
    inner class `given an empty stream` {

        @Nested
        inner class `when appending events` {

            @Test
            fun `then they receive sequential versions`() {
                val store = InMemoryEventStore<String>()

                val appended = store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a", "b")).getOrNull()!!

                assertEquals(listOf(1L, 2L), appended.map { it.version.value })
                assertEquals(listOf("a", "b"), appended.map { it.event })
            }
        }
    }

    @Nested
    inner class `given events appended in two batches` {

        @Nested
        inner class `when reading the stream` {

            @Test
            fun `then events come back in version order`() {
                val store = InMemoryEventStore<String>()
                store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a"))
                store.append(streamId, StreamVersion.INITIAL.next(), nonEmptyListOf("b", "c"))

                val events = store.read(streamId)

                assertEquals(listOf("a", "b", "c"), events.map { it.event })
            }
        }
    }

    @Nested
    inner class `given a stream with events` {

        @Nested
        inner class `when reading after a version` {

            @Test
            fun `then earlier events are excluded`() {
                val store = InMemoryEventStore<String>()
                store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a", "b", "c"))

                val events = store.read(streamId, afterVersion = StreamVersion.INITIAL.next())

                assertEquals(listOf("b", "c"), events.map { it.event })
            }
        }
    }

    @Nested
    inner class `given a stream that moved on` {

        @Nested
        inner class `when appending at a stale expected version` {

            @Test
            fun `then it fails with ConcurrencyConflict`() {
                val store = InMemoryEventStore<String>()
                store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a"))

                val result = store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("b"))

                assertEquals(
                    ConcurrencyConflict(streamId, StreamVersion.INITIAL, StreamVersion.INITIAL.next()),
                    result.leftOrNull(),
                )
            }
        }
    }
}
