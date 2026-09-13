package org.aulune.kuirlibro.domain.ingredient

import org.aulune.commons.repository.GenericRepository

/**
 * Persists and retrieves [Ingredient]s.
 */
interface IngredientRepository : GenericRepository<Ingredient, IngredientId>
