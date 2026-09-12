package org.aulune.kuirlibro.domain.tool

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

/**
 * A [Tool]'s display name.
 *
 * @property value must not be blank, and at most [MAX_LENGTH] characters.
 */
@JvmInline
value class ToolName private constructor(val value: String) {

    /** Holds [ToolName]'s smart constructor. */
    companion object {
        /** The longest a [ToolName] may be. */
        const val MAX_LENGTH = 200

        /**
         * Constructs a [ToolName] from [raw], trimmed, failing if it is blank or longer than
         * [MAX_LENGTH].
         */
        operator fun invoke(raw: String): Either<ToolError, ToolName> = either {
            val trimmed = raw.trim()
            ensure(trimmed.isNotBlank()) { ToolError.BlankName }
            ensure(trimmed.length <= MAX_LENGTH) { ToolError.NameTooLong(trimmed.length) }
            ToolName(trimmed)
        }
    }
}
