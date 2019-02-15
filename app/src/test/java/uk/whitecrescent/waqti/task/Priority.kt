package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.backend.task.CONSTRAINED
import uk.whitecrescent.waqti.backend.task.DEFAULT_PRIORITY
import uk.whitecrescent.waqti.backend.task.DEFAULT_PRIORITY_PROPERTY
import uk.whitecrescent.waqti.backend.task.HIDDEN
import uk.whitecrescent.waqti.backend.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.backend.task.Priority
import uk.whitecrescent.waqti.backend.task.Property
import uk.whitecrescent.waqti.backend.task.SHOWING
import uk.whitecrescent.waqti.backend.task.UNMET
import uk.whitecrescent.waqti.testPriority

@DisplayName("Priority Tests")
class Priority : BaseTaskTest() {

    var priority: Priority = testPriority

    override fun beforeEach() {
        super.beforeEach()
        priority = testPriority
    }

    @DisplayName("Priority Default Values")
    @Test
    fun testTaskPriorityDefaultValues() {
        assertFalse(task.priority.isConstrained)
        assertEquals(DEFAULT_PRIORITY, task.priority.value)
        assertFalse(task.priority.isVisible)
        assertEquals(DEFAULT_PRIORITY_PROPERTY,
                Property(HIDDEN, DEFAULT_PRIORITY, NOT_CONSTRAINED, UNMET))
    }

    @DisplayName("Set Priority Property using setPriorityProperty")
    @Test
    fun testTaskSetPriorityProperty() {
        priority = Priority("TestPriority", 69)

        task.setPriorityProperty(Property(SHOWING, priority, NOT_CONSTRAINED, UNMET))

        assertFalse(task.priority.isConstrained)
        assertEquals(priority, task.priority.value)
        assertTrue(task.priority.isVisible)

        task.hidePriority()
        assertEquals(DEFAULT_PRIORITY_PROPERTY, task.priority)
    }

    @DisplayName("Set Priority Property using setPriorityValue")
    @Test
    fun testTaskSetPriorityValue() {
        priority = Priority("TestPriority", 69)

        task.setPriorityValue(priority)

        assertFalse(task.priority.isConstrained)
        assertEquals(priority, task.priority.value)
        assertTrue(task.priority.isVisible)

        task.hidePriority()
        assertEquals(DEFAULT_PRIORITY_PROPERTY, task.priority)
    }

    @DisplayName("Set Priority Constraint")
    @Test
    fun testTaskSetPriorityConstraint() {
        priority = Priority("TestPriority", 69)

        task.setPriorityProperty(Property(SHOWING, priority, CONSTRAINED, UNMET))

        assertFalse(task.priority.isConstrained)
        assertTrue(task.allUnmetAndShowingConstraints.isEmpty())
        assertEquals(priority, task.priority.value)
        assertTrue(task.priority.isVisible)

    }
}