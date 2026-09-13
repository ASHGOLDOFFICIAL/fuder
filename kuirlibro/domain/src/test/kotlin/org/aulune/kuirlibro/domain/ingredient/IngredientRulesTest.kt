package org.aulune.kuirlibro.domain.ingredient

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.NutritionFacts
import org.aulune.kuirlibro.domain.NutritionValue
import org.aulune.kuirlibro.domain.ObjectKey
import org.junit.jupiter.api.Nested
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

private val id = IngredientId("123e4567-e89b-12d3-a456-426614174000").getOrNull()!!
private val name = IngredientName("Flour").getOrNull()!!
private val otherName = IngredientName("Whole Wheat Flour").getOrNull()!!
private val image = ImageRef(ObjectKey("ingredients/flour.jpg").getOrNull()!!)
private val nutrition = NutritionFacts(
    calories = NutritionValue(BigDecimal("364")).getOrNull()!!,
    protein = NutritionValue(BigDecimal("10.3")).getOrNull()!!,
    fat = NutritionValue(BigDecimal("1")).getOrNull()!!,
    carbs = NutritionValue(BigDecimal("76.3")).getOrNull()!!,
)

private fun created(): Ingredient = IngredientRules.create(id, name, MeasurementKind.MASS, nutrition).getOrNull()!!

class IngredientRulesTest {

    @Nested
    inner class `given ingredient details` {

        @Nested
        inner class `when creating it` {
            private val result = IngredientRules.create(id, name, MeasurementKind.MASS, nutrition)

            @Test
            fun `then an active ingredient with those details is produced`() {
                assertEquals(
                    Ingredient(id, name, MeasurementKind.MASS, nutrition, image = null, IngredientStatus.ACTIVE),
                    result.getOrNull(),
                )
            }
        }
    }

    @Nested
    inner class `given an active ingredient` {
        private val ingredient = created()

        @Nested
        inner class `when updating it` {
            private val result = IngredientRules.update(ingredient, otherName, NutritionFacts.ZERO, image)

            @Test
            fun `then its name, nutrition and image change`() {
                assertEquals(
                    ingredient.copy(name = otherName, nutritionPerUnit = NutritionFacts.ZERO, image = image),
                    result.getOrNull(),
                )
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = IngredientRules.delete(ingredient)

            @Test
            fun `then its status becomes DELETED`() {
                assertEquals(IngredientStatus.DELETED, result.getOrNull()?.status)
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = IngredientRules.undelete(ingredient)

            @Test
            fun `then it fails with NotDeleted`() {
                assertEquals(IngredientError.NotDeleted, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a deleted ingredient` {
        private val ingredient = created().copy(status = IngredientStatus.DELETED)

        @Nested
        inner class `when updating it` {
            private val result = IngredientRules.update(ingredient, otherName, NutritionFacts.ZERO, image)

            @Test
            fun `then it fails with AlreadyDeleted`() {
                assertEquals(IngredientError.AlreadyDeleted, result.leftOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = IngredientRules.undelete(ingredient)

            @Test
            fun `then its status becomes ACTIVE`() {
                assertEquals(IngredientStatus.ACTIVE, result.getOrNull()?.status)
            }
        }
    }
}
