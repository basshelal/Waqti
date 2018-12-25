package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
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
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask
import uk.whitecrescent.waqti.testTimeFuture
import uk.whitecrescent.waqti.testTimePast

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
                        Property(SHOWING, testTimePast, NOT_CONSTRAINED, UNMET)
                )

        assertTrue(task.time.isVisible)
        assertEquals(testTimePast, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(
                Property(SHOWING, testTimePast, NOT_CONSTRAINED, UNMET),
                task.time)

        task.hideTime()
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
    }

    @DisplayName("Set Time Property Value")
    @Test
    fun testTaskSetTimePropertyValue() {
        val task = testTask
                .setTimePropertyValue(testTimePast)

        assertTrue(task.time.isVisible)
        assertEquals(testTimePast, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(
                Property(SHOWING, testTimePast, NOT_CONSTRAINED, UNMET),
                task.time)

        task.hideTime()
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
    }

    @DisplayName("Set Time Constraint")
    @Test
    fun testTaskSetTimeConstraint() {
        val task = testTask
                .setTimeProperty(
                        Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET)
                )

        assertTrue(task.time.isVisible)
        assertEquals(testTimeFuture, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET), task.time)
    }

    @DisplayName("Set Time Constraint Value")
    @Test
    fun testTaskSetTimeConstraintValue() {
        val task = testTask
                .setTimeConstraintValue(testTimeFuture)

        assertTrue(task.time.isVisible)
        assertEquals(testTimeFuture, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET), task.time)
    }

    @DisplayName("Set Time Property before now")
    @Test
    fun testTaskSetTimePropertyBeforeNow() {
        val task = testTask
                .setTimePropertyValue(testTimePast)

        assertTrue(task.time.isVisible)
        assertEquals(testTimePast, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, testTimePast, NOT_CONSTRAINED, UNMET), task.time)

        assertFalse(task.isFailable)

        assertEquals(TaskState.EXISTING, task.state)
    }

    @DisplayName("Set Time Property after now")
    @Test
    fun testTaskSetTimePropertyAfterNow() {
        val task = testTask
                .setTimePropertyValue(testTimeFuture)

        assertTrue(task.time.isVisible)
        assertEquals(testTimeFuture, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, testTimeFuture, NOT_CONSTRAINED, UNMET), task.time)

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
                .setTimeConstraintValue(testTimePast)

        sleep(1.5.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(testTimePast, task.time.value)
        assertTrue(task.time.isConstrained)
        assertTrue(task.time.isMet)

        assertEquals(Property(SHOWING, testTimePast, CONSTRAINED, MET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertTrue(task.getAllShowingConstraints().size == 1)

        // Un-constrain!

        task.setTimeProperty(task.time.unConstrain())

        sleep(2.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(testTimePast, task.time.value)
        assertFalse(task.time.isConstrained)
        assertTrue(task.time.isMet) // doesnt matter

        assertEquals(Property(SHOWING, testTimePast, NOT_CONSTRAINED, MET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
    }

    @DisplayName("Time Un-constraining on Constraint after now")
    @Test
    fun testTaskTimeUnConstrainingAfterNow() {
        val task = testTask
                .setTimeConstraintValue(testTimeFuture)

        sleep(1.5.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(testTimeFuture, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET), task.time)

        assertEquals(TaskState.SLEEPING, task.state)
        assertTrue(task.isFailable)
        assertThrows(TaskStateException::class.java) { task.kill() }
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        // Un-constrain!

        task.setTimeProperty(task.time.unConstrain())

        sleep(2.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(testTimeFuture, task.time.value)
        assertFalse(task.time.isConstrained)
        assertFalse(task.time.isMet) // doesnt matter

        assertEquals(Property(SHOWING, testTimeFuture, NOT_CONSTRAINED, UNMET), task.time)

        assertEquals(TaskState.EXISTING, task.state)
        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
    }

    @DisplayName("Time Constraint Re-Set")
    @Test
    fun testTaskTimeConstraintReSet() {
        val task = testTask
                .setTimeConstraintValue(testTimeFuture)

        sleep(1.5.seconds)

        assertTrue(task.time.isVisible)
        assertEquals(testTimeFuture, task.time.value)
        assertTrue(task.time.isConstrained)
        assertFalse(task.time.isMet)

        assertEquals(Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET), task.time)

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
        val time = testTimeFuture

        val task = testTask
                .setTimeConstraintValue(time)

        assertEquals(time, task.time.value)

        // should this work even when time constraint is met?

        assertThrows(TaskException::class.java) { task.hideTime() }
    }
}