package org.aulune.kuirlibro.domain.tool

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.eventsourcing.core.Decider

/**
 * Decides how commands change a [Tool].
 */
object ToolDecider : Decider<Tool?, ToolCommand, ToolEvent, ToolError> {

    override val initial: Tool? = null

    override fun decide(state: Tool?, command: ToolCommand): Either<ToolError, List<ToolEvent>> = either {
        when (command) {
            is ToolCommand.CreateTool -> {
                ensure(state == null) { ToolError.AlreadyExists }
                listOf(ToolEvent.ToolCreated(command.id, command.name))
            }

            is ToolCommand.RenameTool -> {
                ensureActive(state)
                listOf(ToolEvent.ToolRenamed(command.name))
            }

            is ToolCommand.SetToolImage -> {
                ensureActive(state)
                listOf(ToolEvent.ToolImageSet(command.image))
            }

            ToolCommand.DeleteTool -> {
                ensureActive(state)
                listOf(ToolEvent.ToolDeleted)
            }

            ToolCommand.UndeleteTool -> {
                ensureDeleted(state)
                listOf(ToolEvent.ToolUndeleted)
            }
        }
    }

    override fun evolve(state: Tool?, event: ToolEvent): Tool? = when (event) {
        is ToolEvent.ToolCreated -> Tool(event.id, event.name, image = null, status = ToolStatus.ACTIVE)
        is ToolEvent.ToolRenamed -> state?.copy(name = event.name)
        is ToolEvent.ToolImageSet -> state?.copy(image = event.image)
        ToolEvent.ToolDeleted -> state?.copy(status = ToolStatus.DELETED)
        ToolEvent.ToolUndeleted -> state?.copy(status = ToolStatus.ACTIVE)
    }

    private fun Raise<ToolError>.ensureActive(state: Tool?): Tool {
        ensure(state != null) { ToolError.NotFound }
        ensure(state.status == ToolStatus.ACTIVE) { ToolError.AlreadyDeleted }
        return state
    }

    private fun Raise<ToolError>.ensureDeleted(state: Tool?): Tool {
        ensure(state != null) { ToolError.NotFound }
        ensure(state.status == ToolStatus.DELETED) { ToolError.NotDeleted }
        return state
    }
}
