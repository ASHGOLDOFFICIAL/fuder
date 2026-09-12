package org.aulune.eventsourcing.core

/**
 * The schema version of an aggregate's state shape, used to decide whether a stored
 * [Snapshot] is still usable.
 *
 * @property value must be positive.
 */
@JvmInline
value class SchemaVersion(val value: Int) {
    init {
        require(value > 0) { "Schema version must be positive." }
    }
}
