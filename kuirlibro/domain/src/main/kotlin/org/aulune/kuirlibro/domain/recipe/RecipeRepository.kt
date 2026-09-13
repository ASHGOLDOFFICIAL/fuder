package org.aulune.kuirlibro.domain.recipe

import org.aulune.commons.repository.GenericRepository

/**
 * Persists and retrieves [Recipe]s.
 */
interface RecipeRepository : GenericRepository<Recipe, RecipeId>
