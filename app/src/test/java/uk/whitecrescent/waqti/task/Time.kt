package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.Month
import uk.whitecrescent.waqti.model.at
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.seconds
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.MET
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskException
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.model.time
import uk.whitecrescent.waqti.model.tomorrow
import uk.whitecrescent.waqti.model.yesterday
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask

// Done @ 19-Nov-18 B.Helal
@DisplayName("Time Tests")
class Time : BaseTaskTest() {

    @DisplayName("Time Default Values")
    @Test
    fun testTaskTimeDefaultValues() {
        val task = testTask
        assertFalse(task.time.isConstrained)
        assertEquals(DEFAULT_TIME, task.time.value)
        assertFalse(task.time.isVisible)
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
        assertEquals(DEFAULT_TIME_PROPERTY, Property(HIDDEN, DEFAULT_TIME, NOT_CONSTRAINED, UNMET))
    }

    @DisplayName("Set Time Property")
    @Test
    fun testTaskSetTimeProperty() {
        val task = testTask
                .setTimeProperty(
                        Property(SHOWING, time(1970, Month.JANUARY, 1), NOT_CONSTRAINED, UNMET)
                )

        assertTrue(task.time.isVisible)
        assertEquals(time(1970, Month.JANUARY, 1), task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(
                Property(SHOWING, time(1970, Month.JANUARY, 1), NOT_CONSTRAINED, UNMET),
                task.time)

        task.hideTime()
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
    }

    @DisplayName("Set Time Property Value")
    @Test
    fun testTaskSetTimePropertyValue() {
        val task = testTask
                .setTimePropertyValue(time(1970, Month.JANUARY, 1))

        assertTrue(task.time.isVisible)
        assertEquals(time(1970, Month.JANUARY, 1), task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(
                Property(SHOWING, time(1970, Month.JANUARY, 1), NOT_CONSTRAINED, UNMET),
                task.time)

        task.hideTime()
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
    }

    @DisplayName("Set Time Constraint")
    @Test
    fun testTaskSetTimeConstraint() {
        val task = testTask
                .setTimeProperty(
                        Property(SHOWING, tomorrow at 11, CONSTRAINED, UNMET)
                )

        assertTrue(task.time.isVisible)
        assertEquals(tomorrow at 11, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, tomorrow at 11, CONSTRAINED, UNMET), task.time)
    }

    @DisplayName("Set Time Constraint Value")
    @Test
    fun testTaskSetTimeConstraintValue() {
        val task = testTask
                .setTimeConstraintValue(tomorrow at 11)

        assertTrue(task.time.isVisible)
        assertEquals(tomorrow at 11, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, tomorrow at 11, CONSTRAINED, UNMET), task.time)
    }

    @DisplayName("Set Time Property before now")
    @Test
    fun testTaskSetTimePropertyBeforeNow() {
        val time = now - 3.seconds
        val task = testTask
                .setTimePropertyValue(time)

        assertTrue(task.time.isVisible)
        assertEquals(time, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, time, NOT_CONSTRAINED, UNMET), task.time)

        assertFalse(task.isFailable)

        assertEquals(TaskState.EXISTING, task.state)
    }

    @DisplayName("Set Time Property after now")
    @Test
    fun testTaskSetTimePropertyAfterNow() {
        val time = now + 3.seconds
        val task = testTask
                .setTimePropertyValue(time)

        assertTrue(task.time.isVisible)
        assertEquals(time, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, time, NOT_CONSTRAINED, UNMET), task.time)

        assertFalse(task.isFailable)

        assertEquals(TaskState.EXISTING, task.state)
    }

    @DisplayName("Set Time Constraint before now")
    @Test
    fun testTaskSetTimeConstraintBeforeNow() {
        val time = now - 1.seconds
        val task = testTask
                .setTimeConstraintValue(time)

        assertTrue(task.time.isVisible)
        assertEquals(time, task.time.value)
        assertTrue(task.time.isConstrained)
        assertTrue(task.time.isMet)

        assertEquals(Property(SHOWING, time, CONSTRAINED, MET), task.time)

        assertFalse(task.isFailable)

        assertEquals(TaskState.EXISTING, task.state)

        sleep(1.5.seconds)

        assertEquals(TaskState.EXISTING, task.state)
    }

    @DisplayName("Set Time Constraint after now")
    @Test
    fun testTaskSetTimeConstraintAfterNow() {
        val time = now + 1.seconds
        val task = testTask
                .setTimeConstraintValue(time)

        assertTrue(task.time.isVisible)
        assertEquals(time, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, time, CONSTRAINED, UNMET), task.time)

        assertTrue(task.isFailable)

        assertEquals(TaskState.SLEEPING, task.state)

        sleep(1.5.seconds)

        assertEquals(TaskState.EXISTING, task.state)

        assertTrue(task.time.isVisible)
        assertTrue(task.time.isConstrained)
        assertTrue(task.time.isMet)

    }

    @DisplayName("Set Time Constraint after now on many Tasks")
    @Test
    fun testTaskSetTimeConstraintAfterNowOnManyTasks() {
        val time = now + 1.seconds
        val tasks = getTasks(100)
        tasks.forEach { it.setTimeConstraintValue(time) }

        sleep(2.seconds)

        tasks.forEach { assertTrue(it.state == TaskState.EXISTING) }

    }

    @DisplayName("Time Un-constraining on Constraint before now")
    @Test
    fun testTaskTimeUnConstrainingBeforeNow() {
        val task = testTask
                .setTimeConstraintValue(yesterday at 11)

        sleep(1.5.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(yesterday at 11, task.time.value)
        assertTrue(task.time.isConstrained)
        assertTrue(task.time.isMet)

        assertEquals(Property(SHOWING, yesterday at 11, CONSTRAINED, MET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertTrue(task.getAllShowingConstraints().size == 1)

        // Un-constrain!

        task.setTimeProperty(task.time.unConstrain())

        sleep(2.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(yesterday at 11, task.time.value)
        assertFalse(task.time.isConstrained)
        assertTrue(task.time.isMet) // doesnt matter

        assertEquals(Property(SHOWING, yesterday at 11, NOT_CONSTRAINED, MET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
    }

    @DisplayName("Time Un-constraining on Constraint after now")
    @Test
    fun testTaskTimeUnConstrainingAfterNow() {
        val task = testTask
                .setTimeConstraintValue(tomorrow at 11)

        sleep(1.5.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(tomorrow at 11, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, tomorrow at 11, CONSTRAINED, UNMET), task.time)

        assertEquals(TaskState.SLEEPING, task.state)
        assertTrue(task.isFailable)
        assertThrows(TaskStateException::class.java) { task.kill() }
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        // Un-constrain!

        task.setTimeProperty(task.time.unConstrain())

        sleep(2.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(tomorrow at 11, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet) // doesnt matter

        assertEquals(Property(SHOWING, tomorrow at 11, NOT_CONSTRAINED, UNMET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
    }

    @DisplayName("Time Constraint Re-Set")
    @Test
    fun testTaskTimeConstraintReSet() {
        val task = testTask
                .setTimeConstraintValue(tomorrow at 11)

        sleep(1.5.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(tomorrow at 11, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, tomorrow at 11, CONSTRAINED, UNMET), task.time)

        assertEquals(TaskState.SLEEPING, task.state)
        assertTrue(task.isFailable)
        assertThrows(TaskStateException::class.java) { task.kill() }
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        // Re-set

        val newTime = now + 1.seconds

        task.setTimeConstraintValue(newTime)

        assertEquals(newTime, task.time.value)

        sleep(2.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(newTime, task.time.value)
        assertTrue(task.time.isConstrained)
        assertTrue(task.time.isMet)

        assertEquals(Property(SHOWING, newTime, CONSTRAINED, MET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertTrue(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())

    }

    @DisplayName("Time Constraint Hiding Throws Exception")
    @Test
    fun testTimeConstraintHidingThrowsException() {
        val time = tomorrow at 11

        val task = testTask
                .setTimeConstraintValue(time)

        assertEquals(time, task.time.value)

        // should this work even when time constraint is met?

        assertThrows(TaskException::class.java) { task.hideTime() }
    }
}