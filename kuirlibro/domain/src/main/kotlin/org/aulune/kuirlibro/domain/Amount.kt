package org.aulune.kuirlibro.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * A positive decimal quantity, rounded half-up.
 *
 * @property value must be positive.
 */
@JvmInline
value class Amount private constructor(val value: BigDecimal) {

    /** Sums this amount with [other]. */
    operator fun plus(other: Amount): Amount = Amount(value + other.value)

    /** Multiplies this amount by [factor], failing if [factor] is not positive. */
    operator fun times(factor: BigDecimal): Either<MeasurementError, Amount> = either {
        ensure(factor > BigDecimal.ZERO) { MeasurementError.NonPositiveFactor(factor) }
        Amount(scaled(value * factor))
    }

    /** Holds [Amount]'s smart constructor. */
    companion object {
        private const val SCALE = 3

        private fun scaled(raw: BigDecimal): BigDecimal = raw.setScale(SCALE, RoundingMode.HALF_UP)

        /** Constructs an [Amount] from [raw], failing if it is not positive. */
        operator fun invoke(raw: BigDecimal): Either<MeasurementError, Amount> = either {
            val value = scaled(raw)
            ensure(value > BigDecimal.ZERO) { MeasurementError.NonPositiveAmount(raw) }
            Amount(value)
        }
    }
}
