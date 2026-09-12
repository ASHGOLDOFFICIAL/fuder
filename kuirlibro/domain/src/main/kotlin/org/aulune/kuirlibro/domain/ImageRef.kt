package org.aulune.kuirlibro.domain

/**
 * A reference to an image stored in object storage.
 *
 * @property objectKey where the image is stored.
 */
data class ImageRef(val objectKey: ObjectKey)
