package org.aulune.kuirlibro.domain

import org.junit.jupiter.api.Nested
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class AmountTest {

    @Nested
    inner class `given a positive raw value` {
        private val raw = BigDecimal("1.23456")

        @Nested
        inner class `when constructing` {
            private val amount = Amount(raw).getOrNull()!!

            @Test
            fun `then it rounds to scale 3, half-up`() {
                assertEquals(BigDecimal("1.235"), amount.value)
            }
        }
    }

    @Nested
    inner class `given a non-positive raw value` {
        private val raw = BigDecimal.ZERO

        @Nested
        inner class `when constructing` {
            private val result = Amount(raw)

            @Test
            fun `then it fails with NonPositiveAmount`() {
                assertEquals(MeasurementError.NonPositiveAmount(raw), result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a positive amount` {
        private val amount = Amount(BigDecimal("2")).getOrNull()!!

        @Nested
        inner class `when multiplied by a positive factor` {
            private val result = amount * BigDecimal("1.5")

            @Test
            fun `then the result scales accordingly`() {
                assertEquals(BigDecimal("3.000"), result.getOrNull()?.value)
            }
        }

        @Nested
        inner class `when multiplied by a non-positive factor` {
            private val factor = BigDecimal.ZERO
            private val result = amount * factor

            @Test
            fun `then it fails with NonPositiveFactor`() {
                assertEquals(MeasurementError.NonPositiveFactor(factor), result.leftOrNull())
            }
        }
    }
}
