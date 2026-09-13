package org.aulune.kuirlibro.domain.tool

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.kuirlibro.domain.ImageRef

/**
 * Pure state transitions for a [Tool].
 */
object ToolRules {

    /** A new, active tool with [id] and [name]. */
    fun create(id: ToolId, name: ToolName): Either<ToolError, Tool> = either {
        Tool(id, name, image = null, status = ToolStatus.ACTIVE)
    }

    /** [current] with its name and image replaced by [name] and [image]. */
    fun update(current: Tool, name: ToolName, image: ImageRef?): Either<ToolError, Tool> = either {
        ensureActive(current)
        current.copy(name = name, image = image)
    }

    /** [current] soft-deleted. */
    fun delete(current: Tool): Either<ToolError, Tool> = either {
        ensureActive(current)
        current.copy(status = ToolStatus.DELETED)
    }

    /** [current] restored from a soft delete. */
    fun undelete(current: Tool): Either<ToolError, Tool> = either {
        ensureDeleted(current)
        current.copy(status = ToolStatus.ACTIVE)
    }

    private fun Raise<ToolError>.ensureActive(current: Tool) {
        ensure(current.status == ToolStatus.ACTIVE) { ToolError.AlreadyDeleted }
    }

    private fun Raise<ToolError>.ensureDeleted(current: Tool) {
        ensure(current.status == ToolStatus.DELETED) { ToolError.NotDeleted }
    }
}
