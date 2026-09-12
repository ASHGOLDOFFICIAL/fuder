package org.aulune.eventsourcing.core

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

private class RenameField(
    override val eventType: EventType,
    override val fromVersion: EventVersion,
    override val toVersion: EventVersion,
    private val from: String,
    private val to: String,
) : EventUpcaster<Map<String, String>> {

    override fun upcast(payload: Map<String, String>): Map<String, String> {
        val value = payload.getValue(from)
        return payload - from + (to to value)
    }
}

class UpcasterChainTest {

    private val recipeCreated = EventType("RecipeCreated")

    @Nested
    inner class `given a payload two versions behind` {

        @Nested
        inner class `when upcasting` {

            @Test
            fun `then every step in the chain applies in order`() {
                val chain =
                    UpcasterChain(
                        listOf(
                            RenameField(recipeCreated, EventVersion(1), EventVersion(2), from = "name", to = "title"),
                            RenameField(
                                recipeCreated,
                                EventVersion(2),
                                EventVersion(3),
                                from = "title",
                                to = "displayName",
                            ),
                        ),
                    )

                val (version, payload) = chain.upcast(recipeCreated, EventVersion(1), mapOf("name" to "Pancakes"))

                assertEquals(EventVersion(3), version)
                assertEquals(mapOf("displayName" to "Pancakes"), payload)
            }
        }
    }

    @Nested
    inner class `given a payload already at the current version` {

        @Nested
        inner class `when upcasting` {

            @Test
            fun `then it passes through unchanged`() {
                val chain =
                    UpcasterChain(
                        listOf(RenameField(recipeCreated, EventVersion(1), EventVersion(2), from = "name", to = "title")),
                    )

                val (version, payload) = chain.upcast(recipeCreated, EventVersion(2), mapOf("title" to "Pancakes"))

                assertEquals(EventVersion(2), version)
                assertEquals(mapOf("title" to "Pancakes"), payload)
            }
        }
    }

    @Nested
    inner class `given upcasters for a different event type` {

        @Nested
        inner class `when upcasting` {

            @Test
            fun `then they are ignored`() {
                val chain =
                    UpcasterChain(
                        listOf(
                            RenameField(EventType("ToolCreated"), EventVersion(1), EventVersion(2), from = "name", to = "title"),
                        ),
                    )

                val (version, payload) = chain.upcast(recipeCreated, EventVersion(1), mapOf("name" to "Whisk"))

                assertEquals(EventVersion(1), version)
                assertEquals(mapOf("name" to "Whisk"), payload)
            }
        }
    }
}
