package org.aulune.eventsourcing.core

/**
 * Repeatedly applies the matching upcaster from [upcasters], stopping once none matches
 * the payload's current event type and version.
 */
class UpcasterChain<R>(private val upcasters: List<EventUpcaster<R>>) {
    /**
     * Returns [payload], and the version it started at, upcast for as long as [upcasters]
     * has a match for [eventType] and the payload's current version.
     */
    fun upcast(eventType: EventType, eventVersion: EventVersion, payload: R): Pair<EventVersion, R> {
        var version = eventVersion
        var current = payload
        while (true) {
            val upcaster =
                upcasters.find { it.eventType == eventType && it.fromVersion == version }
                    ?: return version to current
            current = upcaster.upcast(current)
            version = upcaster.toVersion
        }
    }
}
