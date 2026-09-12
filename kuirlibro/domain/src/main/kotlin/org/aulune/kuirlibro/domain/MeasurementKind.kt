package org.aulune.kuirlibro.domain

/**
 * A family of compatible measurement units.
 *
 * Quantities of different kinds can never be converted or combined with each other.
 */
enum class MeasurementKind {
    /** Weight. */
    MASS,

    /** Volume. */
    LIQUID,

    /** A discrete count. */
    QUANTITY,
}
