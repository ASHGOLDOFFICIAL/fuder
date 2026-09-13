package org.aulune.kuirlibro.domain.ingredient

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.NutritionFacts

/**
 * Pure state transitions for an [Ingredient].
 */
object IngredientRules {

    /** A new, active ingredient with [id], [name], [measurementKind] and
     * [nutritionPerUnit]. */
    fun create(
        id: IngredientId,
        name: IngredientName,
        measurementKind: MeasurementKind,
        nutritionPerUnit: NutritionFacts,
    ): Either<IngredientError, Ingredient> = either {
        Ingredient(id, name, measurementKind, nutritionPerUnit, image = null, status = IngredientStatus.ACTIVE)
    }

    /** [current] with its name, nutritional content and image replaced by [name],
     * [nutritionPerUnit] and [image]. */
    fun update(
        current: Ingredient,
        name: IngredientName,
        nutritionPerUnit: NutritionFacts,
        image: ImageRef?,
    ): Either<IngredientError, Ingredient> = either {
        ensureActive(current)
        current.copy(name = name, nutritionPerUnit = nutritionPerUnit, image = image)
    }

    /** [current] soft-deleted. */
    fun delete(current: Ingredient): Either<IngredientError, Ingredient> = either {
        ensureActive(current)
        current.copy(status = IngredientStatus.DELETED)
    }

    /** [current] restored from a soft delete. */
    fun undelete(current: Ingredient): Either<IngredientError, Ingredient> = either {
        ensureDeleted(current)
        current.copy(status = IngredientStatus.ACTIVE)
    }

    private fun Raise<IngredientError>.ensureActive(current: Ingredient) {
        ensure(current.status == IngredientStatus.ACTIVE) { IngredientError.AlreadyDeleted }
    }

    private fun Raise<IngredientError>.ensureDeleted(current: Ingredient) {
        ensure(current.status == IngredientStatus.DELETED) { IngredientError.NotDeleted }
    }
}
