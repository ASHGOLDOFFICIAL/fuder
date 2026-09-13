package org.aulune.kuirlibro.domain.recipe

import java.time.Duration

/**
 * How long a [Recipe] takes to make.
 *
 * @property prep active preparation time, before cooking starts.
 * @property cook cooking time.
 */
data class RecipeTiming(val prep: Duration, val cook: Duration)
