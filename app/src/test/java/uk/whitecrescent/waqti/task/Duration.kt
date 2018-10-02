package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.at
import uk.whitecrescent.waqti.model.days
import uk.whitecrescent.waqti.model.hours
import uk.whitecrescent.waqti.model.minutes
import uk.whitecrescent.waqti.model.seconds
import uk.whitecrescent.waqti.model.secs
import uk.whitecrescent.waqti.model.task.Constraint
import uk.whitecrescent.waqti.model.task.DEFAULT_DURATION
import uk.whitecrescent.waqti.model.task.DEFAULT_DURATION_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskException
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.TimeUnit
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.model.tomorrow
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask

// NOTE: Pretty good, could still maybe improve and add to but good enough for now

/**
 *
 * @author Bassam Helal
 */
@DisplayName("Duration Tests")
class Duration : BaseTaskTest() {

    @DisplayName("TimeUnits")
    @Test
    fun testTimeUnit() {
        val pomodoro = TimeUnit("Pomodoro", 25.minutes)

        assertEquals(50.minutes, TimeUnit.toJavaDuration(pomodoro, 2))

        val halfHour = TimeUnit("Half-Hour", 30.minutes)

        assertEquals(2.hours, TimeUnit.toJavaDuration(halfHour, 4))
    }

    @DisplayName("Duration Default Values")
    @Test
    fun testTaskDurationDefaultValues() {
        val task = testTask

        assertFalse(task.duration is Constraint)
        assertEquals(DEFAULT_DURATION, task.duration.value)
        assertFalse(task.duration.isVisible)
        assertEquals(DEFAULT_DURATION_PROPERTY, Property(HIDDEN, DEFAULT_DURATION))
    }

    @DisplayName("Set Duration Property using setDurationProperty")
    @Test
    fun testTaskSetDurationProperty() {
        val task = testTask
                .setDurationProperty(Property(SHOWING, 7.days))

        assertFalse(task.duration is Constraint)
        assertEquals(7.days, task.duration.value)
        assertTrue(task.duration.isVisible)

        task.hideDuration()
        assertEquals(DEFAULT_DURATION_PROPERTY, task.duration)
    }

    @DisplayName("Set Duration Property using setDurationPropertyValue")
    @Test
    fun testTaskSetDurationPropertyValue() {
        val task = testTask
                .setDurationPropertyValue(7.days)

        assertFalse(task.duration is Constraint)
        assertEquals(7.days, task.duration.value)
        assertTrue(task.duration.isVisible)

        task.hideDuration()
        assertEquals(DEFAULT_DURATION_PROPERTY, task.duration)
    }

    @DisplayName("Set Duration Constraint using setDurationProperty")
    @Test
    fun testTaskSetDurationPropertyWithConstraint() {
        val task = testTask
                .setDurationProperty(
                        Constraint(SHOWING, 7.days, UNMET)
                )

        assertTrue(task.duration is Constraint)
        assertEquals(7.days, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraint")
    @Test
    fun testTaskSetDurationConstraint() {
        val task = testTask
                .setDurationConstraint(
                        Constraint(SHOWING, 7.days, UNMET)
                )

        assertTrue(task.duration is Constraint)
        assertEquals(7.days, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraintValue")
    @Test
    fun testTaskSetDurationConstraintValue() {
        val task = testTask
                .setDurationConstraintValue(7.days)

        assertTrue(task.duration is Constraint)
        assertEquals(7.days, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Property using setDurationProperty with TimeUnits")
    @Test
    fun testTaskSetDurationPropertyWithTimeUnits() {
        val timeUnit = TimeUnit("TestTimeUnit", 10.minutes)

        val task = testTask
                .setDurationPropertyTimeUnits(
                        Property(SHOWING, timeUnit), 6)

        assertFalse(task.duration is Constraint)
        assertEquals(1.hours, task.duration.value)
        assertTrue(task.duration.isVisible)

        task.hideDuration()
        assertEquals(DEFAULT_DURATION_PROPERTY, task.duration)
    }

    @DisplayName("Set Duration Property using setDurationValue with TimeUnits")
    @Test
    fun testTaskSetDurationValueWithTimeUnits() {
        val timeUnit = TimeUnit("TestTimeUnit", 10.minutes)

        val task = testTask
                .setDurationPropertyTimeUnitsValue(timeUnit, 6)

        assertFalse(task.duration is Constraint)
        assertEquals(1.hours, task.duration.value)
        assertTrue(task.duration.isVisible)

        task.hideDuration()
        assertEquals(DEFAULT_DURATION_PROPERTY, task.duration)
    }

    @DisplayName("Set Duration Constraint using setDurationProperty with TimeUnits")
    @Test
    fun testTaskSetDurationPropertyWithConstraintWithTimeUnits() {
        val timeUnit = TimeUnit("TestTimeUnit", 10.minutes)

        val task = testTask
                .setDurationPropertyTimeUnits(
                        Constraint(SHOWING, timeUnit, UNMET), 6)

        assertTrue(task.duration is Constraint)
        assertEquals(1.hours, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraint with TimeUnits")
    @Test
    fun testTaskSetDurationConstraintWithTimeUnits() {
        val timeUnit = TimeUnit("TestTimeUnit", 10.minutes)

        val task = testTask
                .setDurationConstraintTimeUnits(
                        Constraint(SHOWING, timeUnit, UNMET), 6)

        assertTrue(task.duration is Constraint)
        assertEquals(1.hours, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraintValue with TimeUnits")
    @Test
    fun testTaskSetDurationConstraintValueWithTimeUnits() {
        val timeUnit = TimeUnit("TestTimeUnit", 10.minutes)

        val task = testTask
                .setDurationConstraintTimeUnitsValue(timeUnit, 6)

        assertTrue(task.duration is Constraint)
        assertEquals(1.hours, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Property failable")
    @Test
    fun testTaskSetDurationPropertyFailable() {
        val duration = 10.seconds

        val task = testTask
                .setDurationPropertyValue(duration)

        assertFalse(task.isFailable)
        assertFalse(task.duration is Constraint)
        assertEquals(duration, task.duration.value)
        assertTrue(task.duration.isVisible)
    }

    @DisplayName("Set Duration Constraint failable")
    @Test
    fun testTaskSetDurationConstraintFailable() {
        val duration = 1.seconds

        val task = testTask
                .setDurationConstraint(Constraint(SHOWING, duration, UNMET))


        assertTrue(task.isFailable)
        assertTrue(task.duration is Constraint)
        assertEquals(duration, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse(task.duration.asConstraint.isMet)

        assertThrows(TaskStateException::class.java) { task.kill() }

        task.startTimer()

        sleep(2.seconds)

        assertAll({ task.kill() })

        assertTrue(task.state == TaskState.KILLED)
        assertTrue(task.duration.asConstraint.isMet)
    }

    @DisplayName("Set Duration Constraint on many Tasks")
    @Test
    fun testTaskSetDurationConstraintOnManyTasks() {
        val duration = 1.seconds

        val tasks = getTasks(100)
        tasks.forEach {
            it.setDurationConstraintValue(duration)
            it.startTimer()
        }

        sleep(2.seconds)

        assertAll({ tasks.forEach { it.kill() } })

    }

    @DisplayName("Get Duration Left Default")
    @Test
    fun testTaskGetDurationLeftDefault() {
        val task = testTask
        assertThrows(TaskException::class.java) { task.getDurationLeft() }
    }

    @DisplayName("Duration Un-constraining while timer running")
    @Test
    fun testTaskDurationUnConstraining() {
        val task = testTask
                .setDurationConstraintValue(7.days)

        task.startTimer()

        sleep(1.seconds)
        assertThrows(TaskStateException::class.java) { task.kill() }
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setDurationProperty(task.duration.asConstraint.toProperty())

        task.stopTimer()
        assertFalse(task.timerIsRunning())

        task.startTimer()

        sleep(1.seconds)

        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        task.kill()
        assertTrue(task.state == TaskState.KILLED)
    }

    @DisplayName("Duration Constraint Re-Set")
    @Test
    fun testTaskDurationConstraintReSet() {
        val task = testTask
                .setDurationConstraintValue(7.days)

        task.startTimer()

        sleep(1.seconds)
        assertThrows(TaskStateException::class.java) { task.kill() }
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        val newDuration = 1.seconds

        task.stopTimer()

        task.setDurationConstraintValue(newDuration)
        assertEquals(newDuration, task.duration.value)

        task.startTimer()

        sleep(2.seconds)

        task.kill()
        assertTrue(task.state == TaskState.KILLED)
    }

    @DisplayName("Duration Hiding")
    @Test
    fun testDurationHiding() {
        val duration = 7.days

        val task = testTask
                .setDurationPropertyValue(duration)

        assertEquals(duration, task.duration.value)

        task.hideDuration()
        assertEquals(DEFAULT_DURATION_PROPERTY, task.duration)

        task.setDurationConstraintValue(duration)
        assertEquals(duration, task.duration.value)
        assertThrows(IllegalStateException::class.java) { task.hideDuration() }
    }

    @DisplayName("Task Timer Independently")
    @Test
    fun testTaskTimer() {
        val task = testTask

        task.startTimer()
        assertTrue(task.timerIsRunning())
        assertFalse(task.timerIsPaused())
        assertFalse(task.timerIsStopped())

        sleep(1.seconds)

        task.pauseTimer()

        assertTrue(task.timerIsPaused())
        assertFalse(task.timerIsRunning())
        assertFalse(task.timerIsStopped())

        assertTrue(task.timerDuration().secs in 0.95..1.05)

        task.startTimer()

        sleep(1.seconds)

        assertTrue(task.timerDuration().secs in 1.95..2.05)

        task.stopTimer()

        sleep(1.seconds)

        assertTrue(task.timerIsStopped())
        assertFalse(task.timerIsRunning())
        assertFalse(task.timerIsPaused())

        assertEquals(0.0, task.timerDuration().secs)

    }

    @DisplayName("Task Duration Constraint without Timer")
    @Test
    fun testTaskDurationConstraintWithoutTimer() {
        val task = testTask
                .setDurationConstraintValue(1.seconds)

        assertTrue(task.timerIsStopped())
        assertFalse(task.timerIsRunning())

        sleep(2.seconds)

        assertFalse(task.duration.asConstraint.isMet)
        assertTrue(task.getAllUnmetAndShowingConstraints().isNotEmpty())
        assertThrows(TaskStateException::class.java) { task.kill() }

    }

    @DisplayName("Task Duration reset with Timer stop")
    @Test
    fun testTaskDurationReset() {
        val task = testTask
                .setDurationConstraintValue(2.seconds)

        sleep(2.seconds)

        assertEquals(2.seconds, task.getDurationLeft())

        task.startTimer()

        sleep(1.seconds)

        task.stopTimer()

        assertEquals(2.seconds, task.getDurationLeft())
    }

    @DisplayName("Task start Duration when SLEEPING")
    @Test
    fun testTaskStartDurationWhenSLEEPING() {
        val task = testTask
                .setTimeConstraintValue(tomorrow at 16)
                .setDurationConstraintValue(2.seconds)

        assertThrows(TaskStateException::class.java) { task.startTimer() }

        sleep(2.seconds)

        assertEquals(2.seconds, task.getDurationLeft())
    }

    @DisplayName("Task start Duration when KILLED")
    @Test
    fun testTaskStartDurationWhenKILLED() {
        val task = testTask

        task.kill()

        task.setDurationConstraintValue(2.seconds)

        assertThrows(TaskStateException::class.java) { task.startTimer() }

        sleep(2.seconds)

        assertEquals(2.seconds, task.getDurationLeft())
        assertTrue(task.state == TaskState.KILLED)
    }

    @DisplayName("Task start Duration when FAILED")
    @Test
    fun testTaskStartDurationWhenFAILED() {
        val task = testTask
        task.isFailable = true

        task.fail()

        task.setDurationConstraintValue(2.seconds)

        assertThrows(TaskStateException::class.java) { task.startTimer() }

        sleep(2.seconds)

        assertEquals(2.seconds, task.getDurationLeft())
        assertTrue(task.state == TaskState.FAILED)
    }

}