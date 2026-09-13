package org.aulune.kuirlibro.domain.recipe

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

/**
 * A [Recipe]'s display title.
 *
 * @property value must not be blank, and at most [MAX_LENGTH] characters.
 */
@JvmInline
value class RecipeTitle private constructor(val value: String) {

    /** Holds [RecipeTitle]'s smart constructor. */
    companion object {
        /** The longest a [RecipeTitle] may be. */
        const val MAX_LENGTH = 200

        /**
         * Constructs a [RecipeTitle] from [raw], trimmed, failing if it is blank or longer than
         * [MAX_LENGTH].
         */
        operator fun invoke(raw: String): Either<RecipeError, RecipeTitle> = either {
            val trimmed = raw.trim()
            ensure(trimmed.isNotBlank()) { RecipeError.BlankTitle }
            ensure(trimmed.length <= MAX_LENGTH) { RecipeError.TitleTooLong(trimmed.length) }
            RecipeTitle(trimmed)
        }
    }
}
