package org.aulune.kuirlibro.domain.tool

/**
 * Why a command against a [Tool] was rejected.
 */
sealed interface ToolError {

    /**
     * [raw] is not a valid ID.
     *
     * @property raw the rejected value.
     */
    data class InvalidId(val raw: String) : ToolError

    /** A tool name must not be blank. */
    data object BlankName : ToolError

    /**
     * A tool name exceeded [ToolName.MAX_LENGTH].
     *
     * @property length the rejected name's length.
     */
    data class NameTooLong(val length: Int) : ToolError

    /** A tool with this ID already exists. */
    data object AlreadyExists : ToolError

    /** No tool with this ID has been created. */
    data object NotFound : ToolError

    /** The tool has already been deleted. */
    data object AlreadyDeleted : ToolError

    /** The tool is not deleted, so it cannot be undeleted. */
    data object NotDeleted : ToolError
}
