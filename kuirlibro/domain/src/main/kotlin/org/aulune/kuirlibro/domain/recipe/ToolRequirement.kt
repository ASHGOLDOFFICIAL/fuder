package org.aulune.kuirlibro.domain.recipe

/**
 * Whether a [RecipeTool] is essential to making a [Recipe].
 */
enum class ToolRequirement {
    /** The recipe cannot be made without it. */
    REQUIRED,

    /** The recipe can be made without it. */
    OPTIONAL,
}
