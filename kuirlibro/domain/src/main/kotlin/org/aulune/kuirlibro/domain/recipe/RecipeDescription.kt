package org.aulune.kuirlibro.domain.recipe

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

/**
 * A [Recipe]'s free-text description.
 *
 * @property value must not be blank, and at most [MAX_LENGTH] characters.
 */
@JvmInline
value class RecipeDescription private constructor(val value: String) {

    /** Holds [RecipeDescription]'s smart constructor. */
    companion object {
        /** The longest a [RecipeDescription] may be. */
        const val MAX_LENGTH = 2000

        /**
         * Constructs a [RecipeDescription] from [raw], trimmed, failing if it is blank or longer
         * than [MAX_LENGTH].
         */
        operator fun invoke(raw: String): Either<RecipeError, RecipeDescription> = either {
            val trimmed = raw.trim()
            ensure(trimmed.isNotBlank()) { RecipeError.BlankDescription }
            ensure(trimmed.length <= MAX_LENGTH) { RecipeError.DescriptionTooLong(trimmed.length) }
            RecipeDescription(trimmed)
        }
    }
}
