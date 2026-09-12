package org.aulune.kuirlibro.domain.ingredient

/**
 * An [Ingredient]'s lifecycle status.
 */
enum class IngredientStatus {
    /** Visible in normal listings. */
    ACTIVE,

    /** Soft-deleted; hidden unless explicitly requested. */
    DELETED,
}
