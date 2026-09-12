package org.aulune.kuirlibro.domain.tool

import org.aulune.kuirlibro.domain.ImageRef

/**
 * A tool's current state.
 *
 * @property id identifies this tool.
 * @property name this tool's display name.
 * @property image this tool's image, if one has been set.
 * @property status this tool's lifecycle status.
 */
data class Tool(val id: ToolId, val name: ToolName, val image: ImageRef?, val status: ToolStatus)
