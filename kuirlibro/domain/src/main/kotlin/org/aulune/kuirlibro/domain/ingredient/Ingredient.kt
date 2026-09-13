package org.aulune.kuirlibro.domain.ingredient

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind
import org.aulune.kuirlibro.domain.NutritionFacts

/**
 * An ingredient's current state.
 *
 * @property id identifies this ingredient.
 * @property name this ingredient's display name.
 * @property measurementKind fixed at creation; never changes.
 * @property nutritionPerUnit nutritional content per one unit of [measurementKind]'s base unit.
 * @property image this ingredient's image, if one has been set.
 * @property status this ingredient's lifecycle status.
 */
data class Ingredient(
    val id: IngredientId,
    val name: IngredientName,
    val measurementKind: MeasurementKind,
    val nutritionPerUnit: NutritionFacts,
    val image: ImageRef?,
    val status: IngredientStatus,
)
