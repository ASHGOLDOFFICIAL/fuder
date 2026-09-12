package org.aulune.kuirlibro.domain.tool

import arrow.core.Either
import java.util.UUID

/**
 * Identifies a [Tool].
 *
 * @property value the underlying UUID.
 */
@JvmInline
value class ToolId private constructor(val value: UUID) {

    /** Holds [ToolId]'s smart constructor. */
    companion object {
        /** Constructs a [ToolId] from [raw], failing if it is not a valid UUID. */
        operator fun invoke(raw: String): Either<ToolError, ToolId> = Either.catch { UUID.fromString(raw) }
            .mapLeft { ToolError.InvalidId(raw) }
            .map { ToolId(it) }
    }
}
