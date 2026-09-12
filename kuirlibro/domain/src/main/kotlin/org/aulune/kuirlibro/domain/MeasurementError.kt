package org.aulune.kuirlibro.domain

import java.math.BigDecimal

/**
 * Why constructing or combining an [Amount] or a [Quantity] was rejected.
 */
sealed interface MeasurementError {

    /**
     * [raw] is not positive.
     *
     * @property raw the rejected value.
     */
    data class NonPositiveAmount(val raw: BigDecimal) : MeasurementError

    /**
     * A scaling factor is not positive.
     *
     * @property raw the rejected value.
     */
    data class NonPositiveFactor(val raw: BigDecimal) : MeasurementError

    /**
     * Two quantities of different [MeasurementKind]s were compared or combined.
     *
     * @property left the first quantity's kind.
     * @property right the second quantity's kind.
     */
    data class IncompatibleMeasurementKind(val left: MeasurementKind, val right: MeasurementKind) : MeasurementError
}
