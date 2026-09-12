package org.aulune.eventsourcing.core

/**
 * The schema version of a specific event type's payload shape.
 *
 * @property value must be positive.
 */
@JvmInline
value class EventVersion(val value: Int) : Comparable<EventVersion> {
    init {
        require(value > 0) { "Event version must be positive." }
    }

    override fun compareTo(other: EventVersion): Int = value.compareTo(other.value)

    /** Holds [EventVersion] constants. */
    companion object {
        /** The version of an event type that has never been upcast. */
        val INITIAL = EventVersion(1)
    }
}
