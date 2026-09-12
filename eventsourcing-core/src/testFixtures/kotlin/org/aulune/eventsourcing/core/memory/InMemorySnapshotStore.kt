package org.aulune.eventsourcing.core.memory

import org.aulune.eventsourcing.core.Snapshot
import org.aulune.eventsourcing.core.SnapshotStore
import org.aulune.eventsourcing.core.StreamId

/**
 * A [SnapshotStore] backed by an in-memory map.
 *
 * Not safe for concurrent use from multiple threads.
 */
class InMemorySnapshotStore<S> : SnapshotStore<S> {
    private val snapshots = mutableMapOf<StreamId, Snapshot<S>>()

    override fun load(streamId: StreamId): Snapshot<S>? = snapshots[streamId]

    override fun save(streamId: StreamId, snapshot: Snapshot<S>) {
        snapshots[streamId] = snapshot
    }
}
