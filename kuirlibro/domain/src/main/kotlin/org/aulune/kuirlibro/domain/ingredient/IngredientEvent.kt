package org.aulune.kuirlibro.domain.ingredient

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.MeasurementKind

/**
 * Something that happened to an [Ingredient].
 */
sealed interface IngredientEvent {

    /**
     * A new ingredient was created.
     *
     * @property id the new ingredient's ID.
     * @property name the new ingredient's display name.
     * @property measurementKind fixed for this ingredient's lifetime.
     */
    data class IngredientCreated(
        val id: IngredientId,
        val name: IngredientName,
        val measurementKind: MeasurementKind,
    ) : IngredientEvent

    /**
     * An ingredient's display name changed.
     *
     * @property name the new display name.
     */
    data class IngredientRenamed(val name: IngredientName) : IngredientEvent

    /**
     * An ingredient's image was set.
     *
     * @property image the new image.
     */
    data class IngredientImageSet(val image: ImageRef) : IngredientEvent

    /** An ingredient was soft-deleted. */
    data object IngredientDeleted : IngredientEvent

    /** A soft-deleted ingredient was restored. */
    data object IngredientUndeleted : IngredientEvent
}
