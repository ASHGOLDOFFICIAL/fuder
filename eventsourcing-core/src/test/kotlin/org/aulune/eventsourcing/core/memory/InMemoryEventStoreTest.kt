package org.aulune.eventsourcing.core.memory

import arrow.core.nonEmptyListOf
import org.aulune.eventsourcing.core.ConcurrencyConflict
import org.aulune.eventsourcing.core.StreamId
import org.aulune.eventsourcing.core.StreamVersion
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

private val streamId = StreamId("test-1")

class InMemoryEventStoreTest {

    @Nested
    inner class `given an empty stream` {
        private val store = InMemoryEventStore<String>()

        @Nested
        inner class `when appending events` {
            private val appended = store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a", "b")).getOrNull()!!

            @Test
            fun `then they receive sequential versions`() {
                assertEquals(listOf(1L, 2L), appended.map { it.version.value })
                assertEquals(listOf("a", "b"), appended.map { it.event })
            }
        }
    }

    @Nested
    inner class `given events appended in two batches` {
        private val store = InMemoryEventStore<String>()

        init {
            store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a"))
            store.append(streamId, StreamVersion.INITIAL.next(), nonEmptyListOf("b", "c"))
        }

        @Nested
        inner class `when reading the stream` {
            private val events = store.read(streamId)

            @Test
            fun `then events come back in version order`() {
                assertEquals(listOf("a", "b", "c"), events.map { it.event })
            }
        }
    }

    @Nested
    inner class `given a stream with events` {
        private val store = InMemoryEventStore<String>()

        init {
            store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("a", "b", "c"))
        }

        @Nested
        inner class `when reading after a version` {
            private val events = store.read(streamId, afterVersion = StreamVersion.INITIAL.next())

            @Test
            fun `then earlier events are excluded`() {
                assertEquals(listOf("b", "c"), events.map { it.event })
            }
        }

        @Nested
        inner class `when appending at a stale expected version` {
            private val result = store.append(streamId, StreamVersion.INITIAL, nonEmptyListOf("d"))

            @Test
            fun `then it fails with ConcurrencyConflict`() {
                assertEquals(
                    ConcurrencyConflict(streamId, StreamVersion.INITIAL, StreamVersion.INITIAL.next().next().next()),
                    result.leftOrNull(),
                )
            }
        }
    }
}
