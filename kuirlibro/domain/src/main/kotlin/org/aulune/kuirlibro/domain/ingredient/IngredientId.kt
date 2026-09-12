package org.aulune.kuirlibro.domain.ingredient

import arrow.core.Either
import java.util.UUID

/**
 * Identifies an [Ingredient].
 *
 * @property value the underlying UUID.
 */
@JvmInline
value class IngredientId private constructor(val value: UUID) {

    /** Holds [IngredientId]'s smart constructor. */
    companion object {
        /** Constructs an [IngredientId] from [raw], failing if it is not a valid UUID. */
        operator fun invoke(raw: String): Either<IngredientError, IngredientId> = Either.catch { UUID.fromString(raw) }
            .mapLeft { IngredientError.InvalidId(raw) }
            .map { IngredientId(it) }
    }
}
