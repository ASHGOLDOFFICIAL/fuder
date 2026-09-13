package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.MeasurementUnit
import org.aulune.kuirlibro.domain.ingredient.IngredientId

/**
 * Why a command against a [Recipe] was rejected.
 */
sealed interface RecipeError {

    /**
     * [raw] is not a valid ID.
     *
     * @property raw the rejected value.
     */
    data class InvalidId(val raw: String) : RecipeError

    /** A recipe title must not be blank. */
    data object BlankTitle : RecipeError

    /**
     * A recipe title exceeded [RecipeTitle.MAX_LENGTH].
     *
     * @property length the rejected title's length.
     */
    data class TitleTooLong(val length: Int) : RecipeError

    /** A recipe description must not be blank. */
    data object BlankDescription : RecipeError

    /**
     * A recipe description exceeded [RecipeDescription.MAX_LENGTH].
     *
     * @property length the rejected description's length.
     */
    data class DescriptionTooLong(val length: Int) : RecipeError

    /** A recipe with this ID already exists. */
    data object AlreadyExists : RecipeError

    /** No recipe with this ID has been created. */
    data object NotFound : RecipeError

    /** The recipe has already been deleted. */
    data object AlreadyDeleted : RecipeError

    /** The recipe is not deleted, so it cannot be undeleted. */
    data object NotDeleted : RecipeError

    /**
     * An ingredient quantity's unit does not match the ingredient's own measurement kind.
     *
     * @property unit the rejected quantity's unit.
     * @property expectedKind the ingredient's actual measurement kind.
     */
    data class UnitKindMismatch(val unit: MeasurementUnit, val expectedKind: MeasurementKind) : RecipeError

    /**
     * The recipe already has an ingredient with this ID.
     *
     * @property ingredientId the rejected ingredient's ID.
     */
    data class DuplicateIngredient(val ingredientId: IngredientId) : RecipeError

    /**
     * The recipe has no ingredient with this ID.
     *
     * @property ingredientId the rejected ingredient's ID.
     */
    data class IngredientNotFound(val ingredientId: IngredientId) : RecipeError
}
