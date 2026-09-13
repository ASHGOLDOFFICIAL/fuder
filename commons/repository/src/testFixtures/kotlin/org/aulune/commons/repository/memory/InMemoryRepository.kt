package org.aulune.commons.repository.memory

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.aulune.commons.repository.GenericRepository
import org.aulune.commons.repository.RepositoryError

/**
 * A [GenericRepository] backed by an in-memory map.
 *
 * Not safe for concurrent use from multiple threads.
 *
 * @param identify returns an element's identity.
 */
open class InMemoryRepository<E, Id>(private val identify: (E) -> Id) : GenericRepository<E, Id> {
    private val elements = mutableMapOf<Id, E>()

    override suspend fun contains(id: Id): Boolean = elements.containsKey(id)

    override suspend fun persist(elem: E): Either<RepositoryError<Nothing>, E> {
        val id = identify(elem)
        if (elements.containsKey(id)) return RepositoryError.ConstraintViolation(id).left()
        elements[id] = elem
        return elem.right()
    }

    override suspend fun get(id: Id): E? = elements[id]

    override suspend fun <Err> update(id: Id, transform: (E) -> Either<Err, E>): Either<RepositoryError<Err>, E> {
        val current = elements[id] ?: return RepositoryError.FailedPrecondition.left()
        return transform(current).fold(
            ifLeft = { RepositoryError.Declined(it).left() },
            ifRight = {
                elements[id] = it
                it.right()
            },
        )
    }

    override suspend fun delete(id: Id): Either<RepositoryError<Nothing>, Unit> {
        if (!elements.containsKey(id)) return RepositoryError.FailedPrecondition.left()
        elements.remove(id)
        return Unit.right()
    }
}
