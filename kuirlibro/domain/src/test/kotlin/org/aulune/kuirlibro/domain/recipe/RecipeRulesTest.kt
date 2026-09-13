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
private val otherQuantity = Quantity(Amount(BigDecimal("300")).getOrNull()!!, MeasurementUnit.GRAM)
private val wrongKindQuantity = Quantity(Amount(BigDecimal("200")).getOrNull()!!, MeasurementUnit.MILLILITER)

private val steps = listOf(PreparationStep("Mix the batter.", image = null))
private val tools = listOf(RecipeTool(ToolId("323e4567-e89b-12d3-a456-426614174000").getOrNull()!!, ToolRequirement.REQUIRED))

private fun created(): Recipe = RecipeRules.create(id, title, description, mainImage, timing).getOrNull()!!

class RecipeRulesTest {

    @Nested
    inner class `given recipe details` {

        @Nested
        inner class `when creating it` {
            private val result = RecipeRules.create(id, title, description, mainImage, timing)

            @Test
            fun `then an active recipe with those details is produced`() {
                assertEquals(
                    Recipe(id, title, description, mainImage, timing, emptyList(), emptyList(), emptyList(), RecipeStatus.ACTIVE),
                    result.getOrNull(),
                )
            }
        }
    }

    @Nested
    inner class `given an active recipe` {
        private val recipe = created()

        @Nested
        inner class `when updating its details` {
            private val result = RecipeRules.updateDetails(recipe, otherTitle, description, mainImage, otherTiming)

            @Test
            fun `then its title and timing change`() {
                assertEquals(otherTitle, result.getOrNull()?.title)
                assertEquals(otherTiming, result.getOrNull()?.timing)
            }
        }

        @Nested
        inner class `when adding an ingredient` {
            private val result = RecipeRules.addIngredient(recipe, ingredientId, quantity, MeasurementKind.MASS)

            @Test
            fun `then it is added`() {
                assertEquals(listOf(RecipeIngredient(ingredientId, quantity)), result.getOrNull()?.ingredients)
            }
        }

        @Nested
        inner class `when adding an ingredient with a mismatched unit kind` {
            private val result = RecipeRules.addIngredient(recipe, ingredientId, wrongKindQuantity, MeasurementKind.MASS)

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
            private val result = RecipeRules.changeIngredient(recipe, ingredientId, otherQuantity, MeasurementKind.MASS)

            @Test
            fun `then it fails with IngredientNotFound`() {
                assertEquals(RecipeError.IngredientNotFound(ingredientId), result.leftOrNull())
            }
        }

        @Nested
        inner class `when removing an ingredient that is not there` {
            private val result = RecipeRules.removeIngredient(recipe, ingredientId)

            @Test
            fun `then it fails with IngredientNotFound`() {
                assertEquals(RecipeError.IngredientNotFound(ingredientId), result.leftOrNull())
            }
        }

        @Nested
        inner class `when replacing its steps` {
            private val result = RecipeRules.replaceSteps(recipe, steps)

            @Test
            fun `then its steps change`() {
                assertEquals(steps, result.getOrNull()?.steps)
            }
        }

        @Nested
        inner class `when changing its tools` {
            private val result = RecipeRules.changeTools(recipe, tools)

            @Test
            fun `then its tools change`() {
                assertEquals(tools, result.getOrNull()?.tools)
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = RecipeRules.delete(recipe)

            @Test
            fun `then its status becomes DELETED`() {
                assertEquals(RecipeStatus.DELETED, result.getOrNull()?.status)
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = RecipeRules.undelete(recipe)

            @Test
            fun `then it fails with NotDeleted`() {
                assertEquals(RecipeError.NotDeleted, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a recipe with an ingredient` {
        private val recipe = RecipeRules.addIngredient(created(), ingredientId, quantity, MeasurementKind.MASS).getOrNull()!!

        @Nested
        inner class `when adding the same ingredient again` {
            private val result = RecipeRules.addIngredient(recipe, ingredientId, otherQuantity, MeasurementKind.MASS)

            @Test
            fun `then it fails with DuplicateIngredient`() {
                assertEquals(RecipeError.DuplicateIngredient(ingredientId), result.leftOrNull())
            }
        }

        @Nested
        inner class `when changing its quantity` {
            private val result = RecipeRules.changeIngredient(recipe, ingredientId, otherQuantity, MeasurementKind.MASS)

            @Test
            fun `then it changes`() {
                assertEquals(listOf(RecipeIngredient(ingredientId, otherQuantity)), result.getOrNull()?.ingredients)
            }
        }

        @Nested
        inner class `when removing it` {
            private val result = RecipeRules.removeIngredient(recipe, ingredientId)

            @Test
            fun `then it is removed`() {
                assertEquals(emptyList(), result.getOrNull()?.ingredients)
            }
        }
    }

    @Nested
    inner class `given a deleted recipe` {
        private val recipe = created().copy(status = RecipeStatus.DELETED)

        @Nested
        inner class `when updating its details` {
            private val result = RecipeRules.updateDetails(recipe, otherTitle, description, mainImage, otherTiming)

            @Test
            fun `then it fails with AlreadyDeleted`() {
                assertEquals(RecipeError.AlreadyDeleted, result.leftOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = RecipeRules.undelete(recipe)

            @Test
            fun `then its status becomes ACTIVE`() {
                assertEquals(RecipeStatus.ACTIVE, result.getOrNull()?.status)
            }
        }
    }
}
