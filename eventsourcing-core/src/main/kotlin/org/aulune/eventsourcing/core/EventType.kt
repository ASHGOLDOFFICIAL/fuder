package org.aulune.eventsourcing.core

/**
 * Identifies an event's type.
 *
 * @property value must not be blank.
 */
@JvmInline
value class EventType(val value: String) {
    init {
        require(value.isNotBlank()) { "Event type must not be blank." }
    }
}
