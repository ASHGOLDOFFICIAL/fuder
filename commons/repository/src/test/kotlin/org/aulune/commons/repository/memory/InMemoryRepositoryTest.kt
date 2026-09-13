package org.aulune.commons.repository.memory

import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.runBlocking
import org.aulune.commons.repository.RepositoryError
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private data class Elem(val id: Int, val value: String)

class InMemoryRepositoryTest {

    @Nested
    inner class `given no elements` {
        private val repository = InMemoryRepository(Elem::id)

        @Nested
        inner class `when persisting an element` {
            private val result = runBlocking { repository.persist(Elem(1, "a")) }

            @Test
            fun `then it succeeds`() {
                assertEquals(Elem(1, "a"), result.getOrNull())
            }
        }

        @Nested
        inner class `when getting an element` {
            private val result = runBlocking { repository.get(1) }

            @Test
            fun `then null is returned`() {
                assertNull(result)
            }
        }

        @Nested
        inner class `when updating an element` {
            private val result = runBlocking { repository.update(1) { it.right() } }

            @Test
            fun `then it fails with FailedPrecondition`() {
                assertEquals(RepositoryError.FailedPrecondition, result.leftOrNull())
            }
        }

        @Nested
        inner class `when deleting an element` {
            private val result = runBlocking { repository.delete(1) }

            @Test
            fun `then it fails with FailedPrecondition`() {
                assertEquals(RepositoryError.FailedPrecondition, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a persisted element` {
        private val repository = InMemoryRepository(Elem::id)

        init {
            runBlocking { repository.persist(Elem(1, "a")) }
        }

        @Nested
        inner class `when persisting an element with the same identity` {
            private val result = runBlocking { repository.persist(Elem(1, "b")) }

            @Test
            fun `then it fails with ConstraintViolation`() {
                assertEquals(RepositoryError.ConstraintViolation(1), result.leftOrNull())
            }
        }

        @Nested
        inner class `when getting it` {
            private val result = runBlocking { repository.get(1) }

            @Test
            fun `then it is returned`() {
                assertEquals(Elem(1, "a"), result)
            }
        }

        @Nested
        inner class `when updating it with a transform that succeeds` {
            private val result = runBlocking { repository.update(1) { Elem(1, "b").right() } }
            private val stored = runBlocking { repository.get(1) }

            @Test
            fun `then it succeeds`() {
                assertEquals(Elem(1, "b"), result.getOrNull())
            }

            @Test
            fun `then the stored element changes`() {
                assertEquals(Elem(1, "b"), stored)
            }
        }

        @Nested
        inner class `when updating it with a transform that declines` {
            private val result = runBlocking { repository.update(1) { "nope".left() } }
            private val stored = runBlocking { repository.get(1) }

            @Test
            fun `then it fails with Declined`() {
                assertEquals(RepositoryError.Declined("nope"), result.leftOrNull())
            }

            @Test
            fun `then the stored element is unchanged`() {
                assertEquals(Elem(1, "a"), stored)
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = runBlocking { repository.delete(1) }
            private val stored = runBlocking { repository.get(1) }

            @Test
            fun `then it succeeds`() {
                assertEquals(Unit, result.getOrNull())
            }

            @Test
            fun `then it is removed`() {
                assertNull(stored)
            }
        }
    }
}
