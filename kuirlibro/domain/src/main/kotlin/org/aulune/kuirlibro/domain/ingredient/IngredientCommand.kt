package org.aulune.kuirlibro.domain.ingredient

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.NutritionFacts

/**
 * A request to change an [Ingredient].
 */
sealed interface IngredientCommand {

    /**
     * Create a new ingredient.
     *
     * @property id the new ingredient's ID.
     * @property name the new ingredient's display name.
     * @property measurementKind fixed for this ingredient's lifetime.
     * @property nutritionPerUnit nutritional content per one unit of [measurementKind]'s base unit.
     */
    data class CreateIngredient(
        val id: IngredientId,
        val name: IngredientName,
        val measurementKind: MeasurementKind,
        val nutritionPerUnit: NutritionFacts,
    ) : IngredientCommand

    /**
     * Change an ingredient's display name.
     *
     * @property name the new display name.
     */
    data class RenameIngredient(val name: IngredientName) : IngredientCommand

    /**
     * Change an ingredient's nutritional content.
     *
     * @property nutritionPerUnit the new nutritional content per unit.
     */
    data class SetIngredientNutrition(val nutritionPerUnit: NutritionFacts) : IngredientCommand

    /**
     * Set an ingredient's image.
     *
     * @property image the new image.
     */
    data class SetIngredientImage(val image: ImageRef) : IngredientCommand

    /** Soft-delete an ingredient. */
    data object DeleteIngredient : IngredientCommand

    /** Restore a soft-deleted ingredient. */
    data object UndeleteIngredient : IngredientCommand
}
