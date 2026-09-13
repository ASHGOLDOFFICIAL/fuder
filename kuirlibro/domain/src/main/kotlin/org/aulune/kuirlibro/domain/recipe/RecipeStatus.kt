package org.aulune.kuirlibro.domain.recipe

/**
 * A [Recipe]'s lifecycle status.
 */
enum class RecipeStatus {
    /** Visible in normal listings. */
    ACTIVE,

    /** Soft-deleted; hidden unless explicitly requested. */
    DELETED,
}
