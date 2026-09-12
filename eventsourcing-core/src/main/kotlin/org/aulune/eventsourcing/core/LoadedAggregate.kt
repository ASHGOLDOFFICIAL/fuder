package org.aulune.eventsourcing.core

/**
 * An aggregate's state as reconstructed from its stream, and the version it
 * was reconstructed at.
 *
 * @param S the aggregate's state type.
 *
 * @property state the reconstructed state.
 * @property version the stream version [state] was reconstructed at.
 */
data class LoadedAggregate<S>(val state: S, val version: StreamVersion)
