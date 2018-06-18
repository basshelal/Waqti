package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.after
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.Month
import uk.whitecrescent.waqti.model.Time
import uk.whitecrescent.waqti.model.days
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.seconds
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.Constraint
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.model.time
import uk.whitecrescent.waqti.testTask

@DisplayName("Time Tests")
class Time {

    @DisplayName("Time Default Values")
    @Test
    fun testTaskTimeDefaultValues() {
        val task = testTask()
        assertFalse(task.time is Constraint)
        assertEquals(DEFAULT_TIME, task.time.value)
        assertFalse(task.time.isVisible)
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
    }

    @DisplayName("Set Time Property")
    @Test
    fun testTaskSetTimeProperty() {
        val task = testTask()
                .setTimeProperty(
                        Property(SHOWING, time(1970, Month.JANUARY, 1))
                )

        assertFalse(task.time is Constraint)
        assertEquals(time(1970, Month.JANUARY, 1), task.time.value)
        assertTrue(task.time.isVisible)

        task.hideTime()
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)
    }

    @DisplayName("Set Time Property Value")
    @Test
    fun testTaskSetTimeValue() {
        val task = testTask()
                .setTimePropertyValue(
                        Time.of(1970, 1, 1, 1, 1)
                )

        assertFalse(task.time is Constraint)
        assertEquals(Time.of(1970, 1, 1, 1, 1), task.time.value)
        assertTrue(task.time.isVisible)

        task.hideTime()
        assertEquals(Property(HIDDEN, DEFAULT_TIME), task.time)
    }

    @DisplayName("Set Time Constraint Property")
    @Test
    fun testTaskSetTimePropertyWithConstraint() {
        val task = testTask()
                .setTimeProperty(
                        Constraint(SHOWING, time(1970, Month.JANUARY, 1), UNMET)
                )

        assertTrue(task.time is Constraint)
        assertEquals(time(1970, Month.JANUARY, 1), task.time.value)
        assertTrue(task.time.isVisible)
        assertFalse(task.time.asConstraint.isMet)
    }

    @DisplayName("Set Time Constraint")
    @Test
    fun testTaskSetTimeConstraint() {
        val task = testTask()
                .setTimeConstraint(
                        Constraint(SHOWING, time(1970, Month.JANUARY, 1), UNMET)
                )

        assertTrue(task.time is Constraint)
        assertEquals(time(1970, Month.JANUARY, 1), task.time.value)
        assertTrue(task.time.isVisible)
        assertFalse(task.time.asConstraint.isMet)
    }

    @DisplayName("Set Time Constraint Value")
    @Test
    fun testTaskSetTimeConstraintValue() {
        val task = testTask()
                .setTimeConstraintValue(time(1970, Month.JANUARY, 1))

        assertTrue(task.time is Constraint)
        assertEquals(time(1970, Month.JANUARY, 1), task.time.value)
        assertTrue(task.time.isVisible)
        assertFalse(task.time.asConstraint.isMet)
    }

    @DisplayName("Set Time Property before now")
    @Test
    fun testTaskSetTimePropertyBeforeNow() {
        val time = now - 3.seconds
        val task = testTask()
                .setTimePropertyValue(time)

        assertFalse(task.isFailable)
        assertFalse(task.time is Constraint)
        assertEquals(time, task.time.value)
        assertTrue(task.time.isVisible)
    }

    @DisplayName("Set Time Property after now")
    @Test
    fun testTaskSetTimePropertyAfterNow() {
        val time = now + 3.seconds
        val task = testTask()
                .setTimePropertyValue(time)

        assertFalse(task.isFailable)
        assertFalse(task.time is Constraint)
        assertEquals(time, task.time.value)
        assertTrue(task.time.isVisible)
    }

    @DisplayName("Set Time Constraint after now")
    @Test
    fun testTaskSetTimeConstraintAfterNow() {
        val time = now + 2.seconds
        val task = testTask()
                .setTimeConstraint(Constraint(SHOWING, time, UNMET))

        assertTrue(task.state == TaskState.SLEEPING)
        assertTrue(task.isFailable)
        assertTrue(task.time is Constraint)
        assertEquals(time, task.time.value)
        assertTrue(task.time.isVisible)
        assertFalse(task.time.asConstraint.isMet)

        sleep(4)

        assertTrue(task.state == TaskState.EXISTING)
        assertTrue(task.time.asConstraint.isMet)
    }

    @DisplayName("Set Time Constraint before now")
    @Test
    fun testTaskSetTimeConstraintBeforeNow() {
        val time = now - 2.seconds
        val task = testTask()
                .setTimeConstraint(Constraint(SHOWING, time, UNMET))
        assertTrue(task.state != TaskState.SLEEPING)
        assertFalse(task.isFailable)
        assertTrue(task.time is Constraint)
        assertEquals(time, task.time.value)
        assertTrue(task.time.isVisible)
        assertFalse(task.time.asConstraint.isMet)
        sleep(3)
        assertTrue(task.state == TaskState.EXISTING)
    }

    @DisplayName("Set Time Constraint after now on many Tasks")
    @Test
    fun testTaskSetTimeConstraintAfterNowOnManyTasks() {
        val time = now + 3.seconds
        val tasks = getTasks(100)
        tasks.forEach { it.setTimeConstraintValue(time) }

        sleep(4)

        tasks.forEach { assertTrue(it.state == TaskState.EXISTING) }

    }

    @DisplayName("Time Un-constraining")
    @Test
    fun testTaskTimeUnConstraining() {
        val task = testTask()
                .setTimeConstraintValue(now + 7.days)
        sleep(2)
        assertEquals(TaskState.SLEEPING, task.state)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setTimeProperty(task.time.asConstraint.toProperty())

        sleep(2)

        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertEquals(TaskState.EXISTING, task.state)
    }

    @DisplayName("Time Constraint Re-Set")
    @Test
    fun testTaskTimeConstraintReSet() {
        val task = testTask()
                .setTimeConstraintValue(now + 7.days)

        after(1.seconds) {
            assertThrows(TaskStateException::class.java, { task.kill() })
        }
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        val newTime = now + 2.seconds

        task.setTimeConstraintValue(newTime)
        assertEquals(newTime, task.time.value)

        after(4.seconds) {
            assertEquals(TaskState.EXISTING, task.state)
        }
    }

    @DisplayName("Time Hiding")
    @Test
    fun testTimeHiding() {
        val time = Time.from(now + 7.days)

        val task = testTask()
                .setTimePropertyValue(time)
        assertEquals(time, task.time.value)

        task.hideTime()
        assertEquals(DEFAULT_TIME_PROPERTY, task.time)

        task.setTimeConstraintValue(time)
        assertEquals(time, task.time.value)
        assertThrows(IllegalStateException::class.java, { task.hideTime() })
    }
}

