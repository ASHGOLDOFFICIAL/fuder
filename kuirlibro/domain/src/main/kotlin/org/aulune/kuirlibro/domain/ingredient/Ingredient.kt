package org.aulune.kuirlibro.domain.ingredient

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind

/**
 * An ingredient's current state.
 *
 * @property id identifies this ingredient.
 * @property name this ingredient's display name.
 * @property measurementKind fixed at creation; never changes.
 * @property image this ingredient's image, if one has been set.
 * @property status this ingredient's lifecycle status.
 */
data class Ingredient(
    val id: IngredientId,
    val name: IngredientName,
    val measurementKind: MeasurementKind,
    val image: ImageRef?,
    val status: IngredientStatus,
)
