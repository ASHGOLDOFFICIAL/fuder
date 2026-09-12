package org.aulune.kuirlibro.domain

import org.junit.jupiter.api.Nested
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

private fun quantity(raw: String, unit: MeasurementUnit) = Quantity(Amount(BigDecimal(raw)).getOrNull()!!, unit)

class QuantityTest {

    @Nested
    inner class `given a quantity in kilograms` {
        private val quantity = quantity("0.5", MeasurementUnit.KILOGRAM)

        @Nested
        inner class `when converted to grams` {
            private val converted = quantity.convertedTo(MeasurementUnit.GRAM)

            @Test
            fun `then the value is rescaled by the base factor ratio`() {
                val result = converted.getOrNull()!!

                assertEquals(Amount(BigDecimal("500")).getOrNull(), result.amount)
                assertEquals(MeasurementUnit.GRAM, result.unit)
            }
        }

        @Nested
        inner class `when converted to an another unit kind` {
            private val result = quantity.convertedTo(MeasurementUnit.MILLILITER)

            @Test
            fun `then it fails with IncompatibleMeasurementKind`() {
                assertEquals(
                    MeasurementError.IncompatibleMeasurementKind(MeasurementKind.MASS, MeasurementKind.LIQUID),
                    result.leftOrNull(),
                )
            }
        }
    }

    @Nested
    inner class `given two quantities of the same unit kind` {
        private val flour = quantity("0.5", MeasurementUnit.KILOGRAM)
        private val sugar = quantity("100", MeasurementUnit.GRAM)

        @Nested
        inner class `when added together` {
            private val total = flour + sugar

            @Test
            fun `then the result is summed in the kind's base unit`() {
                val result = total.getOrNull()!!

                assertEquals(Amount(BigDecimal("600")).getOrNull(), result.amount)
                assertEquals(MeasurementUnit.GRAM, result.unit)
            }
        }
    }

    @Nested
    inner class `given two quantities of different unit kinds` {
        private val flour = quantity("0.5", MeasurementUnit.KILOGRAM)
        private val milk = quantity("200", MeasurementUnit.MILLILITER)

        @Nested
        inner class `when added together` {
            private val result = flour + milk

            @Test
            fun `then it fails with IncompatibleMeasurementKind`() {
                assertEquals(
                    MeasurementError.IncompatibleMeasurementKind(MeasurementKind.MASS, MeasurementKind.LIQUID),
                    result.leftOrNull(),
                )
            }
        }
    }

    @Nested
    inner class `given a quantity` {
        private val quantity = quantity("2", MeasurementUnit.PIECE)

        @Nested
        inner class `when scaled by a factor` {
            private val scaled = quantity.scaledBy(BigDecimal("1.5"))

            @Test
            fun `then the amount scales and the unit is unchanged`() {
                val result = scaled.getOrNull()!!

                assertEquals(Amount(BigDecimal("3")).getOrNull(), result.amount)
                assertEquals(MeasurementUnit.PIECE, result.unit)
            }
        }
    }
}
