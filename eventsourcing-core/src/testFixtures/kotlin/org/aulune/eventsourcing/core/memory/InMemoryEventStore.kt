package org.aulune.eventsourcing.core.memory

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import org.aulune.eventsourcing.core.ConcurrencyConflict
import org.aulune.eventsourcing.core.EventStore
import org.aulune.eventsourcing.core.StoredEvent
import org.aulune.eventsourcing.core.StreamId
import org.aulune.eventsourcing.core.StreamVersion

/**
 * An [EventStore] backed by an in-memory map.
 *
 * Not safe for concurrent use from multiple threads.
 */
class InMemoryEventStore<E> : EventStore<E> {
    private val streams = mutableMapOf<StreamId, MutableList<StoredEvent<E>>>()

    override fun append(
        streamId: StreamId,
        expectedVersion: StreamVersion,
        events: NonEmptyList<E>,
    ): Either<ConcurrencyConflict, NonEmptyList<StoredEvent<E>>> {
        val stream = streams.getOrPut(streamId) { mutableListOf() }
        val actualVersion = StreamVersion(stream.size.toLong())
        if (actualVersion != expectedVersion) {
            return ConcurrencyConflict(streamId, expectedVersion, actualVersion).left()
        }

        var version = actualVersion
        val appended =
            events.map { event ->
                version = version.next()
                StoredEvent(streamId, version, event)
            }

        stream.addAll(appended)
        return appended.right()
    }

    override fun read(streamId: StreamId, afterVersion: StreamVersion): List<StoredEvent<E>> =
        streams[streamId]?.filter { it.version > afterVersion } ?: emptyList()
}
