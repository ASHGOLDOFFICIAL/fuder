package org.aulune.kuirlibro.domain.recipe

import org.aulune.kuirlibro.domain.ImageRef

/**
 * One step in making a [Recipe]. Ordering is the position in [Recipe.steps].
 *
 * @property instruction what to do.
 * @property image an illustration of this step, if one has been set.
 */
data class PreparationStep(val instruction: String, val image: ImageRef?)
