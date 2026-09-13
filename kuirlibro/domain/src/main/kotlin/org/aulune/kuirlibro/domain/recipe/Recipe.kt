package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.ImageRef

/**
 * A recipe's current state.
 *
 * @property id identifies this recipe.
 * @property title this recipe's display title.
 * @property description this recipe's free-text description, if one has been set.
 * @property mainImage this recipe's image, if one has been set.
 * @property timing this recipe's prep and cook time.
 * @property ingredients this recipe's ingredients.
 * @property steps this recipe's preparation steps, in order.
 * @property tools this recipe's tools.
 * @property status this recipe's lifecycle status.
 */
data class Recipe(
    val id: RecipeId,
    val title: RecipeTitle,
    val description: RecipeDescription?,
    val mainImage: ImageRef?,
    val timing: RecipeTiming,
    val ingredients: List<RecipeIngredient>,
    val steps: List<PreparationStep>,
    val tools: List<RecipeTool>,
    val status: RecipeStatus,
)
