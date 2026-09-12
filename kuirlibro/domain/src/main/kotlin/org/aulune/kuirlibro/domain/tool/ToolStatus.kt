package org.aulune.kuirlibro.domain.tool

/**
 * A [Tool]'s lifecycle status.
 */
enum class ToolStatus {
    /** Visible in normal listings. */
    ACTIVE,

    /** Soft-deleted; hidden unless explicitly requested. */
    DELETED,
}
