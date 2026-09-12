package org.aulune.kuirlibro.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.math.BigDecimal

/**
 * An [amount] expressed in a [unit].
 *
 * @property amount how much.
 * @property unit what it's measured in.
 */
data class Quantity(val amount: Amount, val unit: MeasurementUnit) {

    /** The family [unit] belongs to. */
    val kind: MeasurementKind get() = unit.kind

    /** Re-expresses this quantity in [target], failing if [target] is a different [kind]. */
    fun convertedTo(target: MeasurementUnit): Either<MeasurementError, Quantity> = either {
        ensure(target.kind == kind) { MeasurementError.IncompatibleMeasurementKind(kind, target.kind) }
        val baseValue = amount.value * unit.baseFactor
        val targetValue = baseValue / target.baseFactor
        Quantity(Amount(targetValue).bind(), target)
    }

    /** This quantity multiplied by [factor], in its current unit, failing if [factor] is not positive. */
    fun scaledBy(factor: BigDecimal): Either<MeasurementError, Quantity> = either {
        Quantity(amount.times(factor).bind(), unit)
    }

    /**
     * Sums this quantity with [other], failing if [other] is a different [kind]. The result is
     * expressed in [kind]'s base unit.
     */
    operator fun plus(other: Quantity): Either<MeasurementError, Quantity> = either {
        ensure(other.kind == kind) { MeasurementError.IncompatibleMeasurementKind(kind, other.kind) }
        val base = MeasurementUnit.baseUnitOf(kind)
        val left = convertedTo(base).bind()
        val right = other.convertedTo(base).bind()
        Quantity(left.amount + right.amount, base)
    }
}
