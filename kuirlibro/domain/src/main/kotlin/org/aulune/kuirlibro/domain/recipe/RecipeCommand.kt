package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.Quantity
import org.aulune.kuirlibro.domain.ingredient.IngredientId

/**
 * A request to change a [Recipe].
 */
sealed interface RecipeCommand {

    /**
     * Create a new recipe.
     *
     * @property id the new recipe's ID.
     * @property title the new recipe's display title.
     * @property description the new recipe's description, if any.
     * @property mainImage the new recipe's image, if any.
     * @property timing the new recipe's prep and cook time.
     */
    data class CreateRecipe(
        val id: RecipeId,
        val title: RecipeTitle,
        val description: RecipeDescription?,
        val mainImage: ImageRef?,
        val timing: RecipeTiming,
    ) : RecipeCommand

    /**
     * Change a recipe's title, description, image and timing.
     *
     * @property title the new display title.
     * @property description the new description, if any.
     * @property mainImage the new image, if any.
     * @property timing the new prep and cook time.
     */
    data class UpdateRecipeDetails(
        val title: RecipeTitle,
        val description: RecipeDescription?,
        val mainImage: ImageRef?,
        val timing: RecipeTiming,
    ) : RecipeCommand

    /**
     * Add an ingredient to a recipe.
     *
     * @property ingredientId the ingredient's ID.
     * @property quantity how much of it.
     * @property ingredientKind the ingredient's own measurement kind, read from its current state;
     *   [quantity] must match it.
     */
    data class AddRecipeIngredient(
        val ingredientId: IngredientId,
        val quantity: Quantity,
        val ingredientKind: MeasurementKind,
    ) : RecipeCommand

    /**
     * Change a recipe ingredient's quantity.
     *
     * @property ingredientId the ingredient's ID.
     * @property quantity the new quantity.
     * @property ingredientKind the ingredient's own measurement kind, read from its current state;
     *   [quantity] must match it.
     */
    data class ChangeRecipeIngredient(
        val ingredientId: IngredientId,
        val quantity: Quantity,
        val ingredientKind: MeasurementKind,
    ) : RecipeCommand

    /**
     * Remove an ingredient from a recipe.
     *
     * @property ingredientId the ingredient's ID.
     */
    data class RemoveRecipeIngredient(val ingredientId: IngredientId) : RecipeCommand

    /**
     * Replace a recipe's preparation steps wholesale.
     *
     * @property steps the new steps, in order.
     */
    data class ReplaceRecipeSteps(val steps: List<PreparationStep>) : RecipeCommand

    /**
     * Replace a recipe's tools wholesale.
     *
     * @property tools the new tools.
     */
    data class ChangeRecipeTools(val tools: List<RecipeTool>) : RecipeCommand

    /** Soft-delete a recipe. */
    data object DeleteRecipe : RecipeCommand

    /** Restore a soft-deleted recipe. */
    data object UndeleteRecipe : RecipeCommand
}
