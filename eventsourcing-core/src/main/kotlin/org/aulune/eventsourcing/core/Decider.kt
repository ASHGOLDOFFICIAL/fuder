package org.aulune.eventsourcing.core

import arrow.core.Either

/**
 * Models an event-sourced aggregate as a pure state machine.
 *
 * @param S the aggregate's state.
 * @param C the commands it accepts.
 * @param E the events it produces.
 * @param Err the error a rejected command reports.
 */
interface Decider<S, C, E, Err> {
    /** The state of an aggregate that has no events yet. */
    val initial: S

    /** Validates [command] against [state],
     * producing the events it causes or rejecting it. */
    fun decide(state: S, command: C): Either<Err, List<E>>

    /** Folds [event] into [state], producing the resulting state. */
    fun evolve(state: S, event: E): S
}
