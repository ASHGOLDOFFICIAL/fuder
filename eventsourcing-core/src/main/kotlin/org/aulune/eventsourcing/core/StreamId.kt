package org.aulune.eventsourcing.core

/**
 * Identifies an event stream within the event store.
 *
 * @property value must not be blank.
 */
@JvmInline
value class StreamId(val value: String) {
    init {
        require(value.isNotBlank()) { "Stream ID must not be blank." }
    }
}
