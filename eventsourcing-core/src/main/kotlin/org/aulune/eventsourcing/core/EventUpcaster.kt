package org.aulune.eventsourcing.core

/**
 * Transforms a raw event payload from its shape at [fromVersion]
 * to its shape at [toVersion].
 *
 * @param R the raw payload representation this upcaster transforms.
 */
interface EventUpcaster<R> {
    /** The event type this upcaster applies to. */
    val eventType: EventType

    /** The recorded event schema version this upcaster consumes. */
    val fromVersion: EventVersion

    /** The event schema version [upcast] produces. */
    val toVersion: EventVersion

    /** Transforms [payload] from its shape at [fromVersion]
     * to its shape at [toVersion]. */
    fun upcast(payload: R): R
}
