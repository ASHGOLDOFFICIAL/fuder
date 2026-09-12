package org.aulune.eventsourcing.core

import arrow.core.Either
import arrow.core.NonEmptyList

/**
 * Persists and retrieves the events of a single aggregate type's streams.
 *
 * @param E the aggregate's event type.
 */
interface EventStore<E> {
    /**
     * Appends [events] to [streamId], failing if its actual version
     * does not match [expectedVersion].
     */
    fun append(
        streamId: StreamId,
        expectedVersion: StreamVersion,
        events: NonEmptyList<E>,
    ): Either<ConcurrencyConflict, NonEmptyList<StoredEvent<E>>>

    /**
     * Reads [streamId]'s events recorded after [afterVersion], in version order.
     */
    fun read(streamId: StreamId, afterVersion: StreamVersion = StreamVersion.INITIAL): List<StoredEvent<E>>
}
