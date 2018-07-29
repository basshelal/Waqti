package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.Constraint
import uk.whitecrescent.waqti.model.task.DEFAULT_PRIORITY
import uk.whitecrescent.waqti.model.task.DEFAULT_PRIORITY_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Priority Tests")
class Priority : BaseTaskTest() {

    @DisplayName("Priority Default Values")
    @Test
    fun testTaskPriorityDefaultValues() {
        val task = testTask

        assertFalse(task.priority is Constraint)
        assertEquals(DEFAULT_PRIORITY, task.priority.value)
        assertFalse(task.priority.isVisible)
        assertEquals(DEFAULT_PRIORITY_PROPERTY, Property(HIDDEN, DEFAULT_PRIORITY))
    }

    @DisplayName("Set Priority Property using setPriorityProperty")
    @Test
    fun testTaskSetPriorityProperty() {
        val priority = Priority("TestPriority", 69)

        val task = testTask
                .setPriorityProperty(Property(SHOWING, priority))

        assertFalse(task.priority is Constraint)
        assertEquals(priority, task.priority.value)
        assertTrue(task.priority.isVisible)

        task.hidePriority()
        assertEquals(DEFAULT_PRIORITY_PROPERTY, task.priority)
    }

    @DisplayName("Set Priority Property using setPriorityValue")
    @Test
    fun testTaskSetPriorityValue() {
        val priority = Priority("TestPriority", 69)

        val task = testTask
                .setPriorityValue(priority)

        assertFalse(task.priority is Constraint)
        assertEquals(priority, task.priority.value)
        assertTrue(task.priority.isVisible)

        task.hidePriority()
        assertEquals(DEFAULT_PRIORITY_PROPERTY, task.priority)
    }

    @DisplayName("Set Priority Constraint")
    @Test
    fun testTaskSetPriorityConstraint() {
        val priority = Priority("TestPriority", 69)

        val task = testTask
                .setPriorityProperty(Constraint(SHOWING, priority, UNMET))

        assertFalse(task.priority is Constraint)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertEquals(priority, task.priority.value)
        assertTrue(task.priority.isVisible)
        assertThrows(ClassCastException::class.java)
        { assertTrue(task.priority.asConstraint.isMet) }

    }
}