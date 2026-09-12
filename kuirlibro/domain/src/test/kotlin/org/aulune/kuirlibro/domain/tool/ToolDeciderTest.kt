package org.aulune.kuirlibro.domain.tool

import org.aulune.kuirlibro.domain.ImageRef
import org.aulune.kuirlibro.domain.ObjectKey
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

private val id = ToolId("123e4567-e89b-12d3-a456-426614174000").getOrNull()!!
private val name = ToolName("Whisk").getOrNull()!!
private val otherName = ToolName("Balloon Whisk").getOrNull()!!
private val image = ImageRef(ObjectKey("tools/whisk.jpg").getOrNull()!!)

private fun created(): Tool = ToolDecider.evolve(null, ToolEvent.ToolCreated(id, name))!!

class ToolDeciderTest {

    @Nested
    inner class `given no tool exists` {
        private val state: Tool? = null

        @Nested
        inner class `when creating it` {
            private val result = ToolDecider.decide(state, ToolCommand.CreateTool(id, name))

            @Test
            fun `then ToolCreated is produced`() {
                assertEquals(listOf(ToolEvent.ToolCreated(id, name)), result.getOrNull())
            }
        }

        @Nested
        inner class `when renaming it` {
            private val result = ToolDecider.decide(state, ToolCommand.RenameTool(otherName))

            @Test
            fun `then it fails with NotFound`() {
                assertEquals(ToolError.NotFound, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a tool already exists` {
        private val state = created()

        @Nested
        inner class `when creating it again` {
            private val result = ToolDecider.decide(state, ToolCommand.CreateTool(id, name))

            @Test
            fun `then it fails with AlreadyExists`() {
                assertEquals(ToolError.AlreadyExists, result.leftOrNull())
            }
        }

        @Nested
        inner class `when renaming it` {
            private val result = ToolDecider.decide(state, ToolCommand.RenameTool(otherName))

            @Test
            fun `then ToolRenamed is produced`() {
                assertEquals(listOf(ToolEvent.ToolRenamed(otherName)), result.getOrNull())
            }
        }

        @Nested
        inner class `when setting its image` {
            private val result = ToolDecider.decide(state, ToolCommand.SetToolImage(image))

            @Test
            fun `then ToolImageSet is produced`() {
                assertEquals(listOf(ToolEvent.ToolImageSet(image)), result.getOrNull())
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = ToolDecider.decide(state, ToolCommand.DeleteTool)

            @Test
            fun `then ToolDeleted is produced`() {
                assertEquals(listOf(ToolEvent.ToolDeleted), result.getOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = ToolDecider.decide(state, ToolCommand.UndeleteTool)

            @Test
            fun `then it fails with NotDeleted`() {
                assertEquals(ToolError.NotDeleted, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a deleted tool` {
        private val state = created().copy(status = ToolStatus.DELETED)

        @Nested
        inner class `when renaming it` {
            private val result = ToolDecider.decide(state, ToolCommand.RenameTool(otherName))

            @Test
            fun `then it fails with AlreadyDeleted`() {
                assertEquals(ToolError.AlreadyDeleted, result.leftOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = ToolDecider.decide(state, ToolCommand.UndeleteTool)

            @Test
            fun `then ToolUndeleted is produced`() {
                assertEquals(listOf(ToolEvent.ToolUndeleted), result.getOrNull())
            }
        }
    }
}
