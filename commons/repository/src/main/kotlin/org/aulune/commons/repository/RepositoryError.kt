package org.aulune.commons.repository

/**
 * Errors that can occur in a repository.
 *
 * @param Err a repository operation's own error type, propagated by [Declined].
 */
sealed interface RepositoryError<out Err> {
    /**
     * A persist or update would violate some constraint.
     *
     * @param A the type of constraints.
     * @property constraint the violated constraint.
     */
    data class ConstraintViolation<A>(val constraint: A) : RepositoryError<Nothing>

    /**
     * The operation was rejected because the system is not in a state required for it, e.g.
     * updating an element that doesn't exist.
     */
    data object FailedPrecondition : RepositoryError<Nothing>

    /**
     * A repository operation was declined for its own reason.
     *
     * @param Err the declining operation's own error type.
     * @property error why the operation declined.
     */
    data class Declined<Err>(val error: Err) : RepositoryError<Err>
}
