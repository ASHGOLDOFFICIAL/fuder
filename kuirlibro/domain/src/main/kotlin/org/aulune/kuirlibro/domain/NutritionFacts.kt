package org.aulune.kuirlibro.domain

/**
 * Nutritional content per one unit of an ingredient's measurement kind.
 *
 * @property calories energy.
 * @property protein protein.
 * @property fat fat.
 * @property carbs carbohydrates.
 */
data class NutritionFacts(
    val calories: NutritionValue,
    val protein: NutritionValue,
    val fat: NutritionValue,
    val carbs: NutritionValue,
) {
    /** Holds [NutritionFacts] constants. */
    companion object {
        /** Nutrition facts where every property is zero. */
        val ZERO = NutritionFacts(
            calories = NutritionValue.ZERO,
            protein = NutritionValue.ZERO,
            fat = NutritionValue.ZERO,
            carbs = NutritionValue.ZERO,
        )
    }
}
