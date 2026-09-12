package org.aulune.eventsourcing.core

/**
 * An event as recorded in a stream, at the version it was appended at.
 *
 * @param E the recorded event's type.
 *
 * @property streamId the stream this event was recorded on.
 * @property version the stream version this event was appended at.
 * @property event the recorded event.
 */
data class StoredEvent<out E>(val streamId: StreamId, val version: StreamVersion, val event: E)
