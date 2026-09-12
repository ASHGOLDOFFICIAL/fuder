package org.aulune.kuirlibro.domain.tool

import org.aulune.kuirlibro.domain.ImageRef

/**
 * Something that happened to a [Tool].
 */
sealed interface ToolEvent {

    /**
     * A new tool was created.
     *
     * @property id the new tool's ID.
     * @property name the new tool's display name.
     */
    data class ToolCreated(val id: ToolId, val name: ToolName) : ToolEvent

    /**
     * A tool's display name changed.
     *
     * @property name the new display name.
     */
    data class ToolRenamed(val name: ToolName) : ToolEvent

    /**
     * A tool's image was set.
     *
     * @property image the new image.
     */
    data class ToolImageSet(val image: ImageRef) : ToolEvent

    /** A tool was soft-deleted. */
    data object ToolDeleted : ToolEvent

    /** A soft-deleted tool was restored. */
    data object ToolUndeleted : ToolEvent
}
