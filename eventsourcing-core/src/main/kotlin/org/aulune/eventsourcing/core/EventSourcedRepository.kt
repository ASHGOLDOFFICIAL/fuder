package org.aulune.eventsourcing.core

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.toNonEmptyListOrNull

/**
 * Loads and persists a single aggregate type's state via [decider], [eventStore] and
 * [snapshotStore].
 *
 * @param S the aggregate's state.
 * @param C the commands it accepts.
 * @param E the events it produces.
 * @param Err the error a rejected command reports.
 *
 * @param decider the aggregate's pure decision logic.
 * @param eventStore where the aggregate's events are persisted.
 * @param snapshotStore where the aggregate's snapshots are persisted.
 * @param schemaVersion identifies the current shape of [S].
 * @param snapshotFrequency how many events may accumulate on a stream between snapshots.
 */
class EventSourcedRepository<S, C, E, Err>(
    private val decider: Decider<S, C, E, Err>,
    private val eventStore: EventStore<E>,
    private val snapshotStore: SnapshotStore<S>,
    private val schemaVersion: SchemaVersion,
    private val snapshotFrequency: Int,
) {
    /** Reconstructs [streamId]'s current state. */
    fun load(streamId: StreamId): LoadedAggregate<S> {
        val usableSnapshot = snapshotStore.load(streamId)
            ?.takeIf { it.schemaVersion == schemaVersion }
        val (state, fromVersion) = usableSnapshot?.let { it.state to it.version }
            ?: (decider.initial to StreamVersion.INITIAL)
        val events = eventStore.read(streamId, fromVersion)
        val finalVersion = events.lastOrNull()?.version ?: fromVersion
        return LoadedAggregate(replay(state, events), finalVersion)
    }

    /**
     * Decides [command] against [streamId]'s current state, and appends the resulting
     * events at the loaded version.
     *
     * Returns an empty list without appending anything if the decider produces no events.
     *
     * Fails with [HandleError.Rejected] if the decider rejects the command, or
     * [HandleError.Conflict] if the stream changed since it was loaded.
     */
    fun handle(streamId: StreamId, command: C): Either<HandleError<Err>, List<StoredEvent<E>>> = either {
        val loaded = load(streamId)
        val events = decider
            .decide(loaded.state, command)
            .mapLeft { HandleError.Rejected(it) }
            .bind()
        val nelEvents =
            events.toNonEmptyListOrNull() ?: return@either emptyList()
        val stored = eventStore
            .append(streamId, loaded.version, nelEvents)
            .mapLeft { HandleError.Conflict(it) }
            .bind()
        val newVersion = stored.last().version
        if (crossesSnapshotBoundary(loaded.version, newVersion)) {
            val snapshot = Snapshot(
                replay(loaded.state, stored),
                newVersion,
                schemaVersion,
            )
            snapshotStore.save(streamId, snapshot)
        }
        stored
    }

    private fun replay(state: S, events: List<StoredEvent<E>>): S =
        events.fold(state) { acc, stored -> decider.evolve(acc, stored.event) }

    private fun crossesSnapshotBoundary(before: StreamVersion, after: StreamVersion): Boolean =
        after.value / snapshotFrequency > before.value / snapshotFrequency
}
