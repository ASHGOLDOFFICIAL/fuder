package org.aulune.kuirlibro.domain

/**
 * Why constructing an [ImageRef] was rejected.
 */
sealed interface ImageError {

    /** An object key must not be blank. */
    data object BlankObjectKey : ImageError
}
