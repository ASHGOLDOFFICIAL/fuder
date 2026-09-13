package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.Amount
import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.MeasurementUnit
import org.aulune.kuirlibro.domain.ObjectKey
import org.aulune.kuirlibro.domain.Quantity
import org.aulune.kuirlibro.domain.ingredient.IngredientId
import org.aulune.kuirlibro.domain.tool.ToolId
import org.junit.jupiter.api.Nested
import java.math.BigDecimal
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

private val id = RecipeId("123e4567-e89b-12d3-a456-426614174000").getOrNull()!!
private val title = RecipeTitle("Pancakes").getOrNull()!!
private val otherTitle = RecipeTitle("Fluffy Pancakes").getOrNull()!!
private val description = RecipeDescription("Classic breakfast pancakes.").getOrNull()!!
private val mainImage = ImageRef(ObjectKey("recipes/pancakes.jpg").getOrNull()!!)
private val timing = RecipeTiming(prep = Duration.ofMinutes(10), cook = Duration.ofMinutes(15))
private val otherTiming = RecipeTiming(prep = Duration.ofMinutes(5), cook = Duration.ofMinutes(10))

private val ingredientId = IngredientId("223e4567-e89b-12d3-a456-426614174000").getOrNull()!!
private val quantity = Quantity(Amount(BigDecimal("200")).getOrNull()!!, MeasurementUnit.GRAM)
private val wrongKindQuantity = Quantity(Amount(BigDecimal("200")).getOrNull()!!, MeasurementUnit.MILLILITER)

private val steps = listOf(PreparationStep("Mix the batter.", image = null))
private val tools = listOf(RecipeTool(ToolId("323e4567-e89b-12d3-a456-426614174000").getOrNull()!!, ToolRequirement.REQUIRED))

private fun created(): Recipe = RecipeDecider.evolve(null, RecipeEvent.RecipeCreated(id, title, description, mainImage, timing))!!

private fun withIngredient(): Recipe = RecipeDecider.evolve(created(), RecipeEvent.RecipeIngredientAdded(ingredientId, quantity))!!

class RecipeDeciderTest {

    @Nested
    inner class `given no recipe exists` {
        private val state: Recipe? = null

        @Nested
        inner class `when creating it` {
            private val result = RecipeDecider.decide(state, RecipeCommand.CreateRecipe(id, title, description, mainImage, timing))

            @Test
            fun `then RecipeCreated is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeCreated(id, title, description, mainImage, timing)), result.getOrNull())
            }
        }

        @Nested
        inner class `when updating its details` {
            private val result =
                RecipeDecider.decide(state, RecipeCommand.UpdateRecipeDetails(otherTitle, description, mainImage, otherTiming))

            @Test
            fun `then it fails with NotFound`() {
                assertEquals(RecipeError.NotFound, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a recipe already exists` {
        private val state = created()

        @Nested
        inner class `when creating it again` {
            private val result = RecipeDecider.decide(state, RecipeCommand.CreateRecipe(id, title, description, mainImage, timing))

            @Test
            fun `then it fails with AlreadyExists`() {
                assertEquals(RecipeError.AlreadyExists, result.leftOrNull())
            }
        }

        @Nested
        inner class `when updating its details` {
            private val result =
                RecipeDecider.decide(state, RecipeCommand.UpdateRecipeDetails(otherTitle, description, mainImage, otherTiming))

            @Test
            fun `then RecipeDetailsUpdated is produced`() {
                assertEquals(
                    listOf(RecipeEvent.RecipeDetailsUpdated(otherTitle, description, mainImage, otherTiming)),
                    result.getOrNull(),
                )
            }
        }

        @Nested
        inner class `when adding an ingredient` {
            private val result = RecipeDecider.decide(
                state,
                RecipeCommand.AddRecipeIngredient(ingredientId, quantity, MeasurementKind.MASS),
            )

            @Test
            fun `then RecipeIngredientAdded is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeIngredientAdded(ingredientId, quantity)), result.getOrNull())
            }
        }

        @Nested
        inner class `when adding an ingredient with a mismatched unit kind` {
            private val result = RecipeDecider.decide(
                state,
                RecipeCommand.AddRecipeIngredient(ingredientId, wrongKindQuantity, MeasurementKind.MASS),
            )

            @Test
            fun `then it fails with UnitKindMismatch`() {
                assertEquals(
                    RecipeError.UnitKindMismatch(MeasurementUnit.MILLILITER, MeasurementKind.MASS),
                    result.leftOrNull(),
                )
            }
        }

        @Nested
        inner class `when changing an ingredient that is not there` {
            private val result = RecipeDecider.decide(
                state,
                RecipeCommand.ChangeRecipeIngredient(ingredientId, quantity.scaledBy(BigDecimal.TWO).getOrNull()!!, MeasurementKind.MASS),
            )

            @Test
            fun `then it fails with IngredientNotFound`() {
                assertEquals(RecipeError.IngredientNotFound(ingredientId), result.leftOrNull())
            }
        }

        @Nested
        inner class `when removing an ingredient that is not there` {
            private val result = RecipeDecider.decide(state, RecipeCommand.RemoveRecipeIngredient(ingredientId))

            @Test
            fun `then it fails with IngredientNotFound`() {
                assertEquals(RecipeError.IngredientNotFound(ingredientId), result.leftOrNull())
            }
        }

        @Nested
        inner class `when replacing its steps` {
            private val result = RecipeDecider.decide(state, RecipeCommand.ReplaceRecipeSteps(steps))

            @Test
            fun `then RecipeStepsReplaced is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeStepsReplaced(steps)), result.getOrNull())
            }
        }

        @Nested
        inner class `when changing its tools` {
            private val result = RecipeDecider.decide(state, RecipeCommand.ChangeRecipeTools(tools))

            @Test
            fun `then RecipeToolsChanged is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeToolsChanged(tools)), result.getOrNull())
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = RecipeDecider.decide(state, RecipeCommand.DeleteRecipe)

            @Test
            fun `then RecipeDeleted is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeDeleted), result.getOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = RecipeDecider.decide(state, RecipeCommand.UndeleteRecipe)

            @Test
            fun `then it fails with NotDeleted`() {
                assertEquals(RecipeError.NotDeleted, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a recipe with an ingredient` {
        private val state = withIngredient()

        @Nested
        inner class `when adding the same ingredient again` {
            private val result = RecipeDecider.decide(
                state,
                RecipeCommand.AddRecipeIngredient(ingredientId, quantity.scaledBy(BigDecimal.TWO).getOrNull()!!, MeasurementKind.MASS),
            )

            @Test
            fun `then it fails with DuplicateIngredient`() {
                assertEquals(RecipeError.DuplicateIngredient(ingredientId), result.leftOrNull())
            }
        }

        @Nested
        inner class `when changing its quantity` {
            private val result = RecipeDecider.decide(
                state,
                RecipeCommand.ChangeRecipeIngredient(ingredientId, quantity.scaledBy(BigDecimal.TWO).getOrNull()!!, MeasurementKind.MASS),
            )

            @Test
            fun `then RecipeIngredientChanged is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeIngredientChanged(ingredientId, quantity.scaledBy(BigDecimal.TWO).getOrNull()!!)), result.getOrNull())
            }
        }

        @Nested
        inner class `when removing it` {
            private val result = RecipeDecider.decide(state, RecipeCommand.RemoveRecipeIngredient(ingredientId))

            @Test
            fun `then RecipeIngredientRemoved is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeIngredientRemoved(ingredientId)), result.getOrNull())
            }
        }
    }

    @Nested
    inner class `given a deleted recipe` {
        private val state = created().copy(status = RecipeStatus.DELETED)

        @Nested
        inner class `when updating its details` {
            private val result =
                RecipeDecider.decide(state, RecipeCommand.UpdateRecipeDetails(otherTitle, description, mainImage, otherTiming))

            @Test
            fun `then it fails with AlreadyDeleted`() {
                assertEquals(RecipeError.AlreadyDeleted, result.leftOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = RecipeDecider.decide(state, RecipeCommand.UndeleteRecipe)

            @Test
            fun `then RecipeUndeleted is produced`() {
                assertEquals(listOf(RecipeEvent.RecipeUndeleted), result.getOrNull())
            }
        }
    }
}
