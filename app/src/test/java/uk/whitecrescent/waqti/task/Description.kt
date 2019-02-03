package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Description Tests")
class Description : BaseTaskTest() {

    @DisplayName("Description Default Values")
    @Test
    fun testTaskDescriptionDefaultValues() {
        val task = testTask
        assertFalse(task.description.isConstrained)
        assertEquals(DEFAULT_DESCRIPTION, task.description.value)
        assertFalse(task.description.isVisible)
    }

    @DisplayName("Set Description Property using setDescriptionProperty")
    @Test
    fun testTaskSetDescriptionProperty() {
        val task = testTask
                .setDescriptionProperty(Property(SHOWING, "Test Description", NOT_CONSTRAINED, UNMET))

        assertFalse(task.description.isConstrained)
        assertEquals("Test Description", task.description.value)
        assertTrue(task.description.isVisible)


        task.hideDescription()
        assertEquals(Property(HIDDEN, DEFAULT_DESCRIPTION, NOT_CONSTRAINED, UNMET), task.description)
    }

    @DisplayName("Set Description Property using setDescriptionValue")
    @Test
    fun testTaskSetDescriptionValue() {
        val task = testTask
                .setDescriptionValue("Test Description")

        assertFalse(task.description.isConstrained)
        assertEquals("Test Description", task.description.value)
        assertTrue(task.description.isVisible)

        task.hideDescription()
        assertEquals(Property(HIDDEN, DEFAULT_DESCRIPTION, NOT_CONSTRAINED, UNMET), task.description)
    }

    @DisplayName("Set Description Constraint")
    @Test
    fun testTaskSetDescriptionConstraint() {
        val task = testTask
                .setDescriptionProperty(Property(SHOWING, "Test Description", CONSTRAINED, UNMET))

        assertFalse(task.description.isConstrained)
        assertTrue(task.allUnmetAndShowingConstraints.isEmpty())
        assertEquals("Test Description", task.description.value)
        assertTrue(task.description.isVisible)

    }
}