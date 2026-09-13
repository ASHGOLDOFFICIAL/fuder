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

private fun created(): Tool = ToolRules.create(id, name).getOrNull()!!

class ToolRulesTest {

    @Nested
    inner class `given tool details` {

        @Nested
        inner class `when creating it` {
            private val result = ToolRules.create(id, name)

            @Test
            fun `then an active tool with those details is produced`() {
                assertEquals(Tool(id, name, image = null, ToolStatus.ACTIVE), result.getOrNull())
            }
        }
    }

    @Nested
    inner class `given an active tool` {
        private val tool = created()

        @Nested
        inner class `when updating it` {
            private val result = ToolRules.update(tool, otherName, image)

            @Test
            fun `then its name and image change`() {
                assertEquals(tool.copy(name = otherName, image = image), result.getOrNull())
            }
        }

        @Nested
        inner class `when deleting it` {
            private val result = ToolRules.delete(tool)

            @Test
            fun `then its status becomes DELETED`() {
                assertEquals(ToolStatus.DELETED, result.getOrNull()?.status)
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = ToolRules.undelete(tool)

            @Test
            fun `then it fails with NotDeleted`() {
                assertEquals(ToolError.NotDeleted, result.leftOrNull())
            }
        }
    }

    @Nested
    inner class `given a deleted tool` {
        private val tool = created().copy(status = ToolStatus.DELETED)

        @Nested
        inner class `when updating it` {
            private val result = ToolRules.update(tool, otherName, image)

            @Test
            fun `then it fails with AlreadyDeleted`() {
                assertEquals(ToolError.AlreadyDeleted, result.leftOrNull())
            }
        }

        @Nested
        inner class `when undeleting it` {
            private val result = ToolRules.undelete(tool)

            @Test
            fun `then its status becomes ACTIVE`() {
                assertEquals(ToolStatus.ACTIVE, result.getOrNull()?.status)
            }
        }
    }
}
