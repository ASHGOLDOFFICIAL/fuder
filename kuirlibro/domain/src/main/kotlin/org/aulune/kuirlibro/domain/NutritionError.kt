package org.aulune.kuirlibro.domain

import java.math.BigDecimal

/**
 * Why constructing a [NutritionValue] was rejected.
 */
sealed interface NutritionError {

    /**
     * [raw] is negative.
     *
     * @property raw the rejected value.
     */
    data class NegativeNutritionValue(val raw: BigDecimal) : NutritionError

    /**
     * A scaling factor is not positive.
     *
     * @property raw the rejected value.
     */
    data class NonPositiveFactor(val raw: BigDecimal) : NutritionError
}
