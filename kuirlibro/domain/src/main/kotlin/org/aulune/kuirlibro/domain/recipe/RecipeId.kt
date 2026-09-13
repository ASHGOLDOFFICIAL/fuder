package org.aulune.kuirlibro.domain.recipe

import arrow.core.Either
import java.util.UUID

/**
 * Identifies a [Recipe].
 *
 * @property value the underlying UUID.
 */
@JvmInline
value class RecipeId private constructor(val value: UUID) {

    /** Holds [RecipeId]'s smart constructor. */
    companion object {
        /** Constructs a [RecipeId] from [raw], failing if it is not a valid UUID. */
        operator fun invoke(raw: String): Either<RecipeError, RecipeId> = Either.catch { UUID.fromString(raw) }
            .mapLeft { RecipeError.InvalidId(raw) }
            .map { RecipeId(it) }
    }
}
