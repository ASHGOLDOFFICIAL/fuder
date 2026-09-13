package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.Quantity
import org.aulune.kuirlibro.domain.ingredient.IngredientId

/**
 * Something that happened to a [Recipe].
 */
sealed interface RecipeEvent {

    /**
     * A new recipe was created.
     *
     * @property id the new recipe's ID.
     * @property title the new recipe's display title.
     * @property description the new recipe's description, if set.
     * @property mainImage the new recipe's image, if set.
     * @property timing the new recipe's prep and cook time.
     */
    data class RecipeCreated(
        val id: RecipeId,
        val title: RecipeTitle,
        val description: RecipeDescription?,
        val mainImage: ImageRef?,
        val timing: RecipeTiming,
    ) : RecipeEvent

    /**
     * A recipe's title, description, image or timing changed.
     *
     * @property title the new display title.
     * @property description the new description, if set.
     * @property mainImage the new image, if set.
     * @property timing the new prep and cook time.
     */
    data class RecipeDetailsUpdated(
        val title: RecipeTitle,
        val description: RecipeDescription?,
        val mainImage: ImageRef?,
        val timing: RecipeTiming,
    ) : RecipeEvent

    /**
     * An ingredient was added to a recipe.
     *
     * @property ingredientId the added ingredient's ID.
     * @property quantity how much of it.
     */
    data class RecipeIngredientAdded(val ingredientId: IngredientId, val quantity: Quantity) : RecipeEvent

    /**
     * A recipe ingredient's quantity changed.
     *
     * @property ingredientId the changed ingredient's ID.
     * @property quantity the new quantity.
     */
    data class RecipeIngredientChanged(val ingredientId: IngredientId, val quantity: Quantity) : RecipeEvent

    /**
     * An ingredient was removed from a recipe.
     *
     * @property ingredientId the removed ingredient's ID.
     */
    data class RecipeIngredientRemoved(val ingredientId: IngredientId) : RecipeEvent

    /**
     * A recipe's preparation steps were replaced wholesale.
     *
     * @property steps the new steps, in order.
     */
    data class RecipeStepsReplaced(val steps: List<PreparationStep>) : RecipeEvent

    /**
     * A recipe's tools were replaced wholesale.
     *
     * @property tools the new tools.
     */
    data class RecipeToolsChanged(val tools: List<RecipeTool>) : RecipeEvent

    /** A recipe was soft-deleted. */
    data object RecipeDeleted : RecipeEvent

    /** A soft-deleted recipe was restored. */
    data object RecipeUndeleted : RecipeEvent
}
