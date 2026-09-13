package org.aulune.kuirlibro.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * A non-negative decimal amount in a [NutritionFacts] field, rounded half-up.
 *
 * @property value must not be negative.
 */
@JvmInline
value class NutritionValue private constructor(val value: BigDecimal) {

    /** Sums this nutrition value with [other]. */
    operator fun plus(other: NutritionValue): NutritionValue = NutritionValue(value + other.value)

    /** Multiplies this nutrition value by [factor], failing if [factor] is not positive. */
    operator fun times(factor: BigDecimal): Either<NutritionError, NutritionValue> = either {
        ensure(factor > BigDecimal.ZERO) { NutritionError.NonPositiveFactor(factor) }
        NutritionValue(scaled(value * factor))
    }

    /** Holds [NutritionValue]'s smart constructor. */
    companion object {
        private const val SCALE = 1

        /** Nutrition value of zero. */
        val ZERO = NutritionValue(BigDecimal.ZERO)

        private fun scaled(raw: BigDecimal): BigDecimal = raw.setScale(SCALE, RoundingMode.HALF_UP)

        /** Constructs a [NutritionValue] from [raw], failing if it is negative. */
        operator fun invoke(raw: BigDecimal): Either<NutritionError, NutritionValue> = either {
            val value = scaled(raw)
            ensure(value >= BigDecimal.ZERO) { NutritionError.NegativeNutritionValue(raw) }
            NutritionValue(value)
        }
    }
}
