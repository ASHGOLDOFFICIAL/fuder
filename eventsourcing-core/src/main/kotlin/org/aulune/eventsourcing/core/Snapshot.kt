package org.aulune.eventsourcing.core

/**
 * A saved aggregate state, used to skip replaying an entire stream on load.
 *
 * @param S the aggregate's state type.
 *
 * @property state the aggregate's state at the time it was saved.
 * @property version the stream version [state] was reconstructed at.
 * @property schemaVersion identifies the shape of [state] at the time it was saved.
 */
data class Snapshot<S>(val state: S, val version: StreamVersion, val schemaVersion: SchemaVersion)
