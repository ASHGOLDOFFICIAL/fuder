package org.aulune.kuirlibro.domain.recipe

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.Quantity
import org.aulune.kuirlibro.domain.ingredient.IngredientId

/**
 * Pure state transitions for a [Recipe].
 */
object RecipeRules {

    /** A new, active recipe with [id], [title], [description], [mainImage] and
     * [timing]. */
    fun create(
        id: RecipeId,
        title: RecipeTitle,
        description: RecipeDescription?,
        mainImage: ImageRef?,
        timing: RecipeTiming,
    ): Either<RecipeError, Recipe> = either {
        Recipe(id, title, description, mainImage, timing, emptyList(), emptyList(), emptyList(), RecipeStatus.ACTIVE)
    }

    /** [current] with its title, description, image and timing replaced. */
    fun updateDetails(
        current: Recipe,
        title: RecipeTitle,
        description: RecipeDescription?,
        mainImage: ImageRef?,
        timing: RecipeTiming,
    ): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        current.copy(title = title, description = description, mainImage = mainImage, timing = timing)
    }

    /**
     * [current] with [ingredientId] added at [quantity], failing if [ingredientKind]
     * doesn't match [quantity]'s kind.
     */
    fun addIngredient(
        current: Recipe,
        ingredientId: IngredientId,
        quantity: Quantity,
        ingredientKind: MeasurementKind,
    ): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        ensure(quantity.kind == ingredientKind) { RecipeError.UnitKindMismatch(quantity.unit, ingredientKind) }
        val isDuplicate = current.ingredients.none { it.ingredientId == ingredientId }
        ensure(isDuplicate) { RecipeError.DuplicateIngredient(ingredientId) }
        current.copy(ingredients = current.ingredients + RecipeIngredient(ingredientId, quantity))
    }

    /**
     * [current] with [ingredientId]'s quantity changed to [quantity], failing if
     * [ingredientKind] doesn't match its kind.
     */
    fun changeIngredient(
        current: Recipe,
        ingredientId: IngredientId,
        quantity: Quantity,
        ingredientKind: MeasurementKind,
    ): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        ensure(quantity.kind == ingredientKind) { RecipeError.UnitKindMismatch(quantity.unit, ingredientKind) }
        val exists = current.ingredients.any { it.ingredientId == ingredientId }
        ensure(exists) { RecipeError.IngredientNotFound(ingredientId) }
        val ingredients = current.ingredients.map {
            if (it.ingredientId == ingredientId) RecipeIngredient(ingredientId, quantity) else it
        }
        current.copy(ingredients = ingredients)
    }

    /** [current] with [ingredientId] removed. */
    fun removeIngredient(current: Recipe, ingredientId: IngredientId): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        val exists = current.ingredients.any { it.ingredientId == ingredientId }
        ensure(exists) { RecipeError.IngredientNotFound(ingredientId) }
        current.copy(ingredients = current.ingredients.filterNot { it.ingredientId == ingredientId })
    }

    /** [current] with its preparation steps replaced by [steps]. */
    fun replaceSteps(current: Recipe, steps: List<PreparationStep>): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        current.copy(steps = steps)
    }

    /** [current] with its tools replaced by [tools]. */
    fun changeTools(current: Recipe, tools: List<RecipeTool>): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        current.copy(tools = tools)
    }

    /** [current] soft-deleted. */
    fun delete(current: Recipe): Either<RecipeError, Recipe> = either {
        ensureActive(current)
        current.copy(status = RecipeStatus.DELETED)
    }

    /** [current] restored from a soft delete. */
    fun undelete(current: Recipe): Either<RecipeError, Recipe> = either {
        ensureDeleted(current)
        current.copy(status = RecipeStatus.ACTIVE)
    }

    private fun Raise<RecipeError>.ensureActive(current: Recipe) {
        ensure(current.status == RecipeStatus.ACTIVE) { RecipeError.AlreadyDeleted }
    }

    private fun Raise<RecipeError>.ensureDeleted(current: Recipe) {
        ensure(current.status == RecipeStatus.DELETED) { RecipeError.NotDeleted }
    }
}
