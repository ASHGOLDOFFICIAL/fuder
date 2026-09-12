package org.aulune.kuirlibro.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

/**
 * The key an image is stored under in object storage.
 *
 * @property value must not be blank.
 */
@JvmInline
value class ObjectKey private constructor(val value: String) {

    /** Holds [ObjectKey]'s smart constructor. */
    companion object {
        /** Constructs an [ObjectKey] from [raw], failing if it is blank. */
        operator fun invoke(raw: String): Either<ImageError, ObjectKey> = either {
            ensure(raw.isNotBlank()) { ImageError.BlankObjectKey }
            ObjectKey(raw)
        }
    }
}
