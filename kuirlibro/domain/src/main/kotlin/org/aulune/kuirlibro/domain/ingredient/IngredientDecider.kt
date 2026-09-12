package org.aulune.kuirlibro.domain.ingredient

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.eventsourcing.core.Decider

/**
 * Decides how commands change an [Ingredient].
 */
object IngredientDecider : Decider<Ingredient?, IngredientCommand, IngredientEvent, IngredientError> {

    override val initial: Ingredient? = null

    override fun decide(
        state: Ingredient?,
        command: IngredientCommand,
    ): Either<IngredientError, List<IngredientEvent>> = either {
        when (command) {
            is IngredientCommand.CreateIngredient -> {
                ensure(state == null) { IngredientError.AlreadyExists }
                listOf(IngredientEvent.IngredientCreated(command.id, command.name, command.measurementKind))
            }

            is IngredientCommand.RenameIngredient -> {
                ensureActive(state)
                listOf(IngredientEvent.IngredientRenamed(command.name))
            }

            is IngredientCommand.SetIngredientImage -> {
                ensureActive(state)
                listOf(IngredientEvent.IngredientImageSet(command.image))
            }

            IngredientCommand.DeleteIngredient -> {
                ensureActive(state)
                listOf(IngredientEvent.IngredientDeleted)
            }

            IngredientCommand.UndeleteIngredient -> {
                ensureDeleted(state)
                listOf(IngredientEvent.IngredientUndeleted)
            }
        }
    }

    override fun evolve(state: Ingredient?, event: IngredientEvent): Ingredient? = when (event) {
        is IngredientEvent.IngredientCreated ->
            Ingredient(event.id, event.name, event.measurementKind, image = null, status = IngredientStatus.ACTIVE)

        is IngredientEvent.IngredientRenamed -> state?.copy(name = event.name)

        is IngredientEvent.IngredientImageSet -> state?.copy(image = event.image)

        IngredientEvent.IngredientDeleted -> state?.copy(status = IngredientStatus.DELETED)

        IngredientEvent.IngredientUndeleted -> state?.copy(status = IngredientStatus.ACTIVE)
    }

    private fun Raise<IngredientError>.ensureActive(state: Ingredient?): Ingredient {
        ensure(state != null) { IngredientError.NotFound }
        ensure(state.status == IngredientStatus.ACTIVE) { IngredientError.AlreadyDeleted }
        return state
    }

    private fun Raise<IngredientError>.ensureDeleted(state: Ingredient?): Ingredient {
        ensure(state != null) { IngredientError.NotFound }
        ensure(state.status == IngredientStatus.DELETED) { IngredientError.NotDeleted }
        return state
    }
}
