package org.aulune.kuirlibro.domain.ingredient

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

/**
 * An [Ingredient]'s display name.
 *
 * @property value must not be blank, and at most [MAX_LENGTH] characters.
 */
@JvmInline
value class IngredientName private constructor(val value: String) {

    /** Holds [IngredientName]'s smart constructor. */
    companion object {
        /** The longest an [IngredientName] may be. */
        const val MAX_LENGTH = 200

        /**
         * Constructs an [IngredientName] from [raw], trimmed, failing if it is blank or longer
         * than [MAX_LENGTH].
         */
        operator fun invoke(raw: String): Either<IngredientError, IngredientName> = either {
            val trimmed = raw.trim()
            ensure(trimmed.isNotBlank()) { IngredientError.BlankName }
            ensure(trimmed.length <= MAX_LENGTH) { IngredientError.NameTooLong(trimmed.length) }
            IngredientName(trimmed)
        }
    }
}
