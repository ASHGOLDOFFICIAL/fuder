package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.Quantity
import org.aulune.kuirlibro.domain.ingredient.IngredientId

/**
 * How much of an ingredient a [Recipe] needs.
 *
 * @property ingredientId which ingredient, by reference; never an embedded ingredient.
 * @property quantity how much of it.
 */
data class RecipeIngredient(val ingredientId: IngredientId, val quantity: Quantity)
