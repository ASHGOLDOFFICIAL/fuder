package org.aulune.commons.repository

import arrow.core.Either

/**
 * Basic CRUD operations for structured data.
 *
 * @param E the element type.
 * @param Id the element's identity type.
 */
interface GenericRepository<E, Id> {
    /** Checks whether an element identified by [id] is persisted. */
    suspend fun contains(id: Id): Boolean

    /**
     * Persists [elem], failing with [RepositoryError.ConstraintViolation] on any constraint
     * violation, including an element with the same identity already being persisted.
     */
    suspend fun persist(elem: E): Either<RepositoryError<Nothing>, E>

    /** Retrieves the element identified by [id], or `null` if none is persisted. */
    suspend fun get(id: Id): E?

    /**
     * Atomically applies [transform] to the persisted element identified by [id] and persists the
     * result, failing with [RepositoryError.FailedPrecondition] if there is none,
     * [RepositoryError.Declined] if [transform] itself fails, or
     * [RepositoryError.ConstraintViolation] on any constraint violation.
     */
    suspend fun <Err> update(id: Id, transform: (E) -> Either<Err, E>): Either<RepositoryError<Err>, E>

    /** Deletes the element identified by [id], failing with [RepositoryError.FailedPrecondition] if there is none. */
    suspend fun delete(id: Id): Either<RepositoryError<Nothing>, Unit>
}
