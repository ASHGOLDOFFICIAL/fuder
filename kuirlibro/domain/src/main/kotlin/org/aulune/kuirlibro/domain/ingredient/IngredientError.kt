package org.aulune.kuirlibro.domain.ingredient

/**
 * Why a command against an [Ingredient] was rejected.
 */
sealed interface IngredientError {

    /**
     * [raw] is not a valid ID.
     *
     * @property raw the rejected value.
     */
    data class InvalidId(val raw: String) : IngredientError

    /** An ingredient name must not be blank. */
    data object BlankName : IngredientError

    /**
     * An ingredient name exceeded [IngredientName.MAX_LENGTH].
     *
     * @property length the rejected name's length.
     */
    data class NameTooLong(val length: Int) : IngredientError

    /** An ingredient with this ID already exists. */
    data object AlreadyExists : IngredientError

    /** No ingredient with this ID has been created. */
    data object NotFound : IngredientError

    /** The ingredient has already been deleted. */
    data object AlreadyDeleted : IngredientError

    /** The ingredient is not deleted, so it cannot be undeleted. */
    data object NotDeleted : IngredientError
}
