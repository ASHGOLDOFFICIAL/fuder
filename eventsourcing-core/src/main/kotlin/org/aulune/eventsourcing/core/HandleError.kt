package org.aulune.eventsourcing.core

/**
 * Why [EventSourcedRepository.handle] failed to record a command's effects.
 */
sealed interface HandleError<out Err> {
    /**
     * The command was rejected by the aggregate's [Decider].
     *
     * @param Err the decider's error type.
     * @property error the decider's rejection reason.
     */
    data class Rejected<Err>(val error: Err) : HandleError<Err>

    /**
     * The stream changed between load and append; the caller may retry with fresh state.
     *
     * @property conflict details of the version mismatch.
     */
    data class Conflict(val conflict: ConcurrencyConflict) : HandleError<Nothing>
}
