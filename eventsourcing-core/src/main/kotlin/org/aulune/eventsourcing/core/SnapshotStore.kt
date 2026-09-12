package org.aulune.eventsourcing.core

/**
 * Persists and retrieves the latest snapshot of a single aggregate type's streams.
 *
 * @param S the aggregate's state type.
 */
interface SnapshotStore<S> {
    /** Returns [streamId]'s latest snapshot, or `null` if none has been saved yet. */
    fun load(streamId: StreamId): Snapshot<S>?

    /** Replaces [streamId]'s snapshot with [snapshot]. */
    fun save(streamId: StreamId, snapshot: Snapshot<S>)
}
