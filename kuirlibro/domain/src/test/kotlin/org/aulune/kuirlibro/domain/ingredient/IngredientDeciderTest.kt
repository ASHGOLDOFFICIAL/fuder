package org.aulune.kuirlibro.domain.ingredient

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.ObjectKey
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

private val id = IngredientId("123e4567-e89b-12d3-a456-426614174000").getOrNull()!!
private val name = IngredientName("Flour").getOrNull()!!
private val otherName = IngredientName("Whole Wheat Flour").getOrNull()!!
private val image = ImageRef(ObjectKey("ingredients/flour.jpg").getOrNull()!!)

private fun created(): Ingredient = IngredientDecider.evolve(null, IngredientEvent.IngredientCreated(id, name, MeasurementKind.MASS))!!

class IngredientDeciderTest {

    @Nested
    inner class `given no ingredient exists` {
        private val state: Ingredient? = null

        @Nested
        inner class `when creating it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.CreateIngredient(id, name, MeasurementKind.MASS))

            @Test
            fun `then IngredientCreated is produced`() {
                assertEquals(
                    listOf(IngredientEvent.IngredientCreated(id, name, MeasurementKind.MASS)),
                    result.getOrNull(),
                )
            }
        }

        @Nested
        inner class `when renaming it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.RenameIngredient(otherName))

            @Test
            fun `then it fails with NotFound`() {
                assertEquals(IngredientError.NotFound, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given an ingredient already exists` {
        private val state = created()

        @Nested
        inner class `when creating it again` {
            private val result =
                IngredientDecider.decide(state, IngredientCommand.CreateIngredient(id, name, MeasurementKind.MASS))

            @Test
            fun `then it fails with AlreadyExists`() {
                assertEquals(IngredientError.AlreadyExists, result.leftOrNull())
            }
        }

        @Nested
        inner class `when renaming it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.RenameIngredient(otherName))

            @Test
            fun `then IngredientRenamed is produced`() {
                assertEquals(listOf(IngredientEvent.IngredientRenamed(otherName)), result.getOrNull())
            }
        }

        @Nested
        inner class `when setting its image` {
            private val result = IngredientDecider.decide(state, IngredientCommand.SetIngredientImage(image))

            @Test
            fun `then IngredientImageSet is produced`() {
                assertEquals(listOf(IngredientEvent.IngredientImageSet(image)), result.getOrNull())
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.DeleteIngredient)

            @Test
            fun `then IngredientDeleted is produced`() {
                assertEquals(listOf(IngredientEvent.IngredientDeleted), result.getOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.UndeleteIngredient)

            @Test
            fun `then it fails with NotDeleted`() {
                assertEquals(IngredientError.NotDeleted, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a deleted ingredient` {
        private val state = created().copy(status = IngredientStatus.DELETED)

        @Nested
        inner class `when renaming it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.RenameIngredient(otherName))

            @Test
            fun `then it fails with AlreadyDeleted`() {
                assertEquals(IngredientError.AlreadyDeleted, result.leftOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = IngredientDecider.decide(state, IngredientCommand.UndeleteIngredient)

            @Test
            fun `then IngredientUndeleted is produced`() {
                assertEquals(listOf(IngredientEvent.IngredientUndeleted), result.getOrNull())
            }
        }
    }
}
