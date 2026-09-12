package org.aulune.kuirlibro.domain

import java.math.BigDecimal

private const val KILO_FACTOR = 1000
private val KILO = BigDecimal(KILO_FACTOR)

/**
 * A concrete unit an [Amount] can be expressed in.
 *
 * @property kind the family of units this unit converts within.
 * @property baseFactor how many of [kind]'s base unit one of this unit is worth.
 */
enum class MeasurementUnit(val kind: MeasurementKind, val baseFactor: BigDecimal) {
    /** Grams. */
    GRAM(MeasurementKind.MASS, BigDecimal.ONE),

    /** Kilograms. */
    KILOGRAM(MeasurementKind.MASS, KILO),

    /** Milliliters. */
    MILLILITER(MeasurementKind.LIQUID, BigDecimal.ONE),

    /** Liters. */
    LITER(MeasurementKind.LIQUID, KILO),

    /** A single piece. */
    PIECE(MeasurementKind.QUANTITY, BigDecimal.ONE),
    ;

    /** Holds [MeasurementUnit] lookups. */
    companion object {
        /** The unit [kind]'s conversions and sums are expressed in terms of. */
        fun baseUnitOf(kind: MeasurementKind): MeasurementUnit = when (kind) {
            MeasurementKind.MASS -> GRAM
            MeasurementKind.LIQUID -> MILLILITER
            MeasurementKind.QUANTITY -> PIECE
        }
    }
}
