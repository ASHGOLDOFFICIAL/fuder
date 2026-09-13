package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.tool.ToolId

/**
 * A tool a [Recipe] uses.
 *
 * @property toolId which tool, by reference; never an embedded tool.
 * @property requirement whether the recipe can be made without it.
 */
data class RecipeTool(val toolId: ToolId, val requirement: ToolRequirement)
