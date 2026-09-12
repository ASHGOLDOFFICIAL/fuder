package org.aulune.eventsourcing.core

/**
 * Raised when an append attempt targets a stream whose actual version does not match
 * [expectedVersion].
 *
 * @property streamId ID of the stream this append attempt targeted.
 * @property expectedVersion the version the caller expected the stream to be at.
 * @property actualVersion the stream's actual version.
 */
data class ConcurrencyConflict(
    val streamId: StreamId,
    val expectedVersion: StreamVersion,
    val actualVersion: StreamVersion,
)
