package org.aulune.kuirlibro.domain.recipe

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.aulune.eventsourcing.core.Decider

/**
 * Decides how commands change a [Recipe].
 */
object RecipeDecider : Decider<Recipe?, RecipeCommand, RecipeEvent, RecipeError> {

    override val initial: Recipe? = null

    override fun decide(state: Recipe?, command: RecipeCommand): Either<RecipeError, List<RecipeEvent>> = either {
        when (command) {
            is RecipeCommand.CreateRecipe -> create(state, command)

            is RecipeCommand.UpdateRecipeDetails -> updateDetails(state, command)

            is RecipeCommand.AddRecipeIngredient -> addIngredient(state, command)

            is RecipeCommand.ChangeRecipeIngredient -> changeIngredient(state, command)

            is RecipeCommand.RemoveRecipeIngredient -> removeIngredient(state, command)

            is RecipeCommand.ReplaceRecipeSteps -> {
                ensureActive(state)
                listOf(RecipeEvent.RecipeStepsReplaced(command.steps))
            }

            is RecipeCommand.ChangeRecipeTools -> {
                ensureActive(state)
                listOf(RecipeEvent.RecipeToolsChanged(command.tools))
            }

            RecipeCommand.DeleteRecipe -> {
                ensureActive(state)
                listOf(RecipeEvent.RecipeDeleted)
            }

            RecipeCommand.UndeleteRecipe -> {
                ensureDeleted(state)
                listOf(RecipeEvent.RecipeUndeleted)
            }
        }
    }

    private fun Raise<RecipeError>.create(state: Recipe?, command: RecipeCommand.CreateRecipe): List<RecipeEvent> {
        ensure(state == null) { RecipeError.AlreadyExists }
        return listOf(
            RecipeEvent.RecipeCreated(
                command.id,
                command.title,
                command.description,
                command.mainImage,
                command.timing,
            ),
        )
    }

    private fun Raise<RecipeError>.updateDetails(
        state: Recipe?,
        command: RecipeCommand.UpdateRecipeDetails,
    ): List<RecipeEvent> {
        ensureActive(state)
        return listOf(
            RecipeEvent.RecipeDetailsUpdated(command.title, command.description, command.mainImage, command.timing),
        )
    }

    private fun Raise<RecipeError>.addIngredient(
        state: Recipe?,
        command: RecipeCommand.AddRecipeIngredient,
    ): List<RecipeEvent> {
        val recipe = ensureActive(state)
        ensure(command.quantity.kind == command.ingredientKind) {
            RecipeError.UnitKindMismatch(command.quantity.unit, command.ingredientKind)
        }
        ensure(recipe.ingredients.none { it.ingredientId == command.ingredientId }) {
            RecipeError.DuplicateIngredient(command.ingredientId)
        }
        return listOf(RecipeEvent.RecipeIngredientAdded(command.ingredientId, command.quantity))
    }

    private fun Raise<RecipeError>.changeIngredient(
        state: Recipe?,
        command: RecipeCommand.ChangeRecipeIngredient,
    ): List<RecipeEvent> {
        val recipe = ensureActive(state)
        ensure(command.quantity.kind == command.ingredientKind) {
            RecipeError.UnitKindMismatch(command.quantity.unit, command.ingredientKind)
        }
        ensure(recipe.ingredients.any { it.ingredientId == command.ingredientId }) {
            RecipeError.IngredientNotFound(command.ingredientId)
        }
        return listOf(RecipeEvent.RecipeIngredientChanged(command.ingredientId, command.quantity))
    }

    private fun Raise<RecipeError>.removeIngredient(
        state: Recipe?,
        command: RecipeCommand.RemoveRecipeIngredient,
    ): List<RecipeEvent> {
        val recipe = ensureActive(state)
        ensure(recipe.ingredients.any { it.ingredientId == command.ingredientId }) {
            RecipeError.IngredientNotFound(command.ingredientId)
        }
        return listOf(RecipeEvent.RecipeIngredientRemoved(command.ingredientId))
    }

    override fun evolve(state: Recipe?, event: RecipeEvent): Recipe? = when (event) {
        is RecipeEvent.RecipeCreated -> Recipe(
            id = event.id,
            title = event.title,
            description = event.description,
            mainImage = event.mainImage,
            timing = event.timing,
            ingredients = emptyList(),
            steps = emptyList(),
            tools = emptyList(),
            status = RecipeStatus.ACTIVE,
        )

        is RecipeEvent.RecipeDetailsUpdated -> state?.copy(
            title = event.title,
            description = event.description,
            mainImage = event.mainImage,
            timing = event.timing,
        )

        is RecipeEvent.RecipeIngredientAdded -> state?.copy(
            ingredients = state.ingredients + RecipeIngredient(event.ingredientId, event.quantity),
        )

        is RecipeEvent.RecipeIngredientChanged -> state?.copy(
            ingredients = state.ingredients.map {
                if (it.ingredientId == event.ingredientId) RecipeIngredient(event.ingredientId, event.quantity) else it
            },
        )

        is RecipeEvent.RecipeIngredientRemoved -> state?.copy(
            ingredients = state.ingredients.filterNot { it.ingredientId == event.ingredientId },
        )

        is RecipeEvent.RecipeStepsReplaced -> state?.copy(steps = event.steps)

        is RecipeEvent.RecipeToolsChanged -> state?.copy(tools = event.tools)

        RecipeEvent.RecipeDeleted -> state?.copy(status = RecipeStatus.DELETED)

        RecipeEvent.RecipeUndeleted -> state?.copy(status = RecipeStatus.ACTIVE)
    }

    private fun Raise<RecipeError>.ensureActive(state: Recipe?): Recipe {
        ensure(state != null) { RecipeError.NotFound }
        ensure(state.status == RecipeStatus.ACTIVE) { RecipeError.AlreadyDeleted }
        return state
    }

    private fun Raise<RecipeError>.ensureDeleted(state: Recipe?): Recipe {
        ensure(state != null) { RecipeError.NotFound }
        ensure(state.status == RecipeStatus.DELETED) { RecipeError.NotDeleted }
        return state
    }
}
