package org.aulune.kuirlibro.domain.tool

import org.aulune.kuirlibro.domain.ImageRef

/**
 * A request to change a [Tool].
 */
sealed interface ToolCommand {

    /**
     * Create a new tool.
     *
     * @property id the new tool's ID.
     * @property name the new tool's display name.
     */
    data class CreateTool(val id: ToolId, val name: ToolName) : ToolCommand

    /**
     * Change a tool's display name.
     *
     * @property name the new display name.
     */
    data class RenameTool(val name: ToolName) : ToolCommand

    /**
     * Set a tool's image.
     *
     * @property image the new image.
     */
    data class SetToolImage(val image: ImageRef) : ToolCommand

    /** Soft-delete a tool. */
    data object DeleteTool : ToolCommand

    /** Restore a soft-deleted tool. */
    data object UndeleteTool : ToolCommand
}
