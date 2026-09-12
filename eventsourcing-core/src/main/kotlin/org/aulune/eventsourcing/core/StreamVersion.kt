package org.aulune.eventsourcing.core

/**
 * The version of an event stream: the count of events appended to it so far.
 *
 * @property value must not be negative.
 */
@JvmInline
value class StreamVersion(val value: Long) : Comparable<StreamVersion> {
    init {
        require(value >= 0) { "Stream version must not be negative." }
    }

    /** The version one event after this one. */
    fun next(): StreamVersion = StreamVersion(value + 1)

    override fun compareTo(other: StreamVersion): Int = value.compareTo(other.value)

    /** Holds [StreamVersion] constants. */
    companion object {
        /** The version of a stream with no events yet. */
        val INITIAL = StreamVersion(0)
    }
}
