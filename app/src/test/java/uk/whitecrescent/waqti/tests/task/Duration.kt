package uk.whitecrescent.waqti.tests.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.today
import uk.whitecrescent.waqti.task.Constraint
import uk.whitecrescent.waqti.task.DEFAULT_DURATION
import uk.whitecrescent.waqti.task.DEFAULT_DURATION_PROPERTY
import uk.whitecrescent.waqti.task.HIDDEN
import uk.whitecrescent.waqti.task.Property
import uk.whitecrescent.waqti.task.SHOWING
import uk.whitecrescent.waqti.task.TaskState
import uk.whitecrescent.waqti.task.TaskStateException
import uk.whitecrescent.waqti.task.TimeUnit
import uk.whitecrescent.waqti.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Duration Tests")
class Duration {

    // Before All
    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            TimeUnit.getOrCreateTimeUnit("TestTimeUnit", Duration.ofMinutes(10))
        }
    }

    @DisplayName("TimeUnits")
    @Test
    fun testTimeUnit() {
        TimeUnit.getOrCreateTimeUnit("Pomodoro", Duration.ofMinutes(25))
        assertEquals(Duration.ofMinutes(50),
                TimeUnit.toJavaDuration(
                        TimeUnit.getTimeUnit("Pomodoro", Duration.ofMinutes(25)),
                        2))
        TimeUnit.getOrCreateTimeUnit("Half-Hour", Duration.ofMinutes(30))
        assertEquals(Duration.ofHours(2),
                TimeUnit.toJavaDuration(
                        TimeUnit.getTimeUnit("Half-Hour", Duration.ofMinutes(30)),
                        4))
    }

    @DisplayName("Duration Default Values")
    @Test
    fun testTaskDurationDefaultValues() {
        val task = testTask()
        assertFalse(task.duration is Constraint)
        assertEquals(DEFAULT_DURATION, task.duration.value)
        assertFalse(task.duration.isVisible)
    }

    @DisplayName("Set Duration Property using setDurationProperty")
    @Test
    fun testTaskSetDurationProperty() {
        val task = testTask()
                .setDurationProperty(
                        Property(SHOWING, Duration.ofDays(7))
                )

        assertFalse(task.duration is Constraint)
        assertEquals(Duration.ofDays(7), task.duration.value)
        assertTrue(task.duration.isVisible)


        task.hideDuration()
        assertEquals(Property(HIDDEN, DEFAULT_DURATION), task.duration)
    }

    @DisplayName("Set Duration Property using setDurationPropertyValue")
    @Test
    fun testTaskSetDurationPropertyValue() {
        val task = testTask()
                .setDurationPropertyValue(
                        Duration.ofDays(7)
                )

        assertFalse(task.duration is Constraint)
        assertEquals(Duration.ofDays(7), task.duration.value)
        assertTrue(task.duration.isVisible)

        task.hideDuration()
        assertEquals(Property(HIDDEN, DEFAULT_DURATION), task.duration)
    }

    @DisplayName("Set Duration Constraint using setDurationProperty")
    @Test
    fun testTaskSetDurationPropertyWithConstraint() {
        val task = testTask()
                .setDurationProperty(
                        Constraint(SHOWING, Duration.ofDays(7), UNMET)
                )

        assertTrue(task.duration is Constraint)
        assertEquals(Duration.ofDays(7), task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraint")
    @Test
    fun testTaskSetDurationConstraint() {
        val task = testTask()
                .setDurationConstraint(
                        Constraint(SHOWING, Duration.ofDays(7), UNMET)
                )

        assertTrue(task.duration is Constraint)
        assertEquals(Duration.ofDays(7), task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraintValue")
    @Test
    fun testTaskSetDurationConstraintValue() {
        val task = testTask()
                .setDurationConstraintValue(Duration.ofDays(7))

        assertTrue(task.duration is Constraint)
        assertEquals(Duration.ofDays(7), task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Property using setDurationProperty with TimeUnits")
    @Test
    fun testTaskSetDurationPropertyWithTimeUnits() {
        val task = testTask()
                .setDurationPropertyTimeUnits(
                        Property(SHOWING, TimeUnit.getTimeUnit("TestTimeUnit", Duration.ofMinutes(10))),
                        6
                )

        assertFalse(task.duration is Constraint)
        assertEquals(Duration.ofHours(1), task.duration.value)
        assertTrue(task.duration.isVisible)


        task.hideDuration()
        assertEquals(Property(HIDDEN, DEFAULT_DURATION), task.duration)
    }

    @DisplayName("Set Duration Property using setDurationValue with TimeUnits")
    @Test
    fun testTaskSetDurationValueWithTimeUnits() {
        val task = testTask()
                .setDurationPropertyTimeUnitsValue(
                        TimeUnit.getTimeUnit("TestTimeUnit", Duration.ofMinutes(10)),
                        6
                )

        assertFalse(task.duration is Constraint)
        assertEquals(Duration.ofHours(1), task.duration.value)
        assertTrue(task.duration.isVisible)

        task.hideDuration()
        assertEquals(Property(HIDDEN, DEFAULT_DURATION), task.duration)
    }

    @DisplayName("Set Duration Constraint using setDurationProperty with TimeUnits")
    @Test
    fun testTaskSetDurationPropertyWithConstraintWithTimeUnits() {
        val task = testTask()
                .setDurationPropertyTimeUnits(
                        Constraint(SHOWING, TimeUnit.getTimeUnit("TestTimeUnit", Duration.ofMinutes(10)), UNMET),
                        6
                )

        assertTrue(task.duration is Constraint)
        assertEquals(Duration.ofHours(1), task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraint with TimeUnits")
    @Test
    fun testTaskSetDurationConstraintWithTimeUnits() {
        val task = testTask()
                .setDurationConstraintTimeUnits(
                        Constraint(SHOWING, TimeUnit.getTimeUnit("TestTimeUnit", Duration.ofMinutes(10)), UNMET),
                        6
                )

        assertTrue(task.duration is Constraint)
        assertEquals(Duration.ofHours(1), task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Constraint using setDurationConstraintValue with TimeUnits")
    @Test
    fun testTaskSetDurationConstraintValueWithTimeUnits() {
        val task = testTask()
                .setDurationConstraintTimeUnitsValue(TimeUnit.getTimeUnit("TestTimeUnit", Duration.ofMinutes(10)), 6)

        assertTrue(task.duration is Constraint)
        assertEquals(Duration.ofHours(1), task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Property failable")
    @Test
    fun testTaskSetDurationPropertyFailable() {
        val duration = Duration.ofSeconds(10)
        val task = testTask()
                .setDurationPropertyValue(duration)

        assertFalse(task.isFailable)
        assertFalse(task.duration is Constraint)
        assertEquals(duration, task.duration.value)
        assertTrue(task.duration.isVisible)
    }

    @DisplayName("Set Duration Constraint failable")
    @Test
    fun testTaskSetDurationConstraintFailable() {
        val duration = Duration.ofSeconds(2)
        val task = testTask()
                .setDurationConstraint(Constraint(SHOWING, duration, UNMET))


        assertTrue(task.isFailable)
        assertTrue(task.duration is Constraint)
        assertEquals(duration, task.duration.value)
        assertTrue(task.duration.isVisible)
        assertFalse((task.duration as Constraint).isMet)
        assertThrows(TaskStateException::class.java, { task.kill() })

        task.startTimer()

        sleep(4)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
        assertTrue((task.duration as Constraint).isMet)
    }

    @DisplayName("Set Duration Constraint on many Tasks")
    @Test
    fun testTaskSetDurationConstraintOnManyTasks() {
        val duration = Duration.ofSeconds(2)
        val tasks = getTasks(100)
        tasks.forEach {
            it.setDurationConstraintValue(duration)
            it.startTimer()
        }

        sleep(4)

        tasks.forEach { it.kill() }

    }

    @DisplayName("Get Duration Left Default")
    @Test
    fun testTaskGetDurationLeftDefault() {
        val task = testTask()
        assertThrows(IllegalStateException::class.java, { task.getDurationLeft() })
    }

    @DisplayName("Duration Un-constraining")
    @Test
    fun testTaskDurationUnConstraining() {
        val task = testTask()
                .setDurationConstraintValue(Duration.ofDays(7))

        task.startTimer()

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setDurationProperty((task.duration as Constraint).toProperty())

        task.stopTimer()
        assertTrue(!task.timerIsRunning())

        task.startTimer()

        sleep(1)

        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Duration Constraint Re-Set")
    @Test
    fun testTaskDurationConstraintReSet() {
        val task = testTask()
                .setDurationConstraintValue(Duration.ofDays(7))

        task.startTimer()

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        val newDuration = Duration.ofSeconds(2)

        task.stopTimer()

        task.setDurationConstraintValue(newDuration)
        assertEquals(newDuration, task.duration.value)

        task.startTimer()

        sleep(4)

        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Duration Hiding")
    @Test
    fun testDurationHiding() {
        val duration = Duration.ofDays(7)

        val task = testTask()
                .setDurationPropertyValue(duration)
        assertEquals(duration, task.duration.value)

        task.hideDuration()
        assertEquals(DEFAULT_DURATION_PROPERTY, task.duration)

        task.setDurationConstraintValue(duration)
        assertEquals(duration, task.duration.value)
        assertThrows(IllegalStateException::class.java, { task.hideDuration() })
    }

    @DisplayName("Task Timer Independently")
    @Test
    fun testTaskTimer() {
        val task = testTask()

        task.startTimer()
        assertTrue(task.timerIsRunning())
        assertFalse(task.timerIsPaused())
        assertFalse(task.timerIsStopped())

        sleep(2)

        task.pauseTimer()

        assertTrue(task.timerIsPaused())
        assertFalse(task.timerIsRunning())
        assertFalse(task.timerIsStopped())

        assertTrue(task.timerDuration().seconds >= 1.999 || task.timerDuration().seconds <= 2.001)

        task.startTimer()

        sleep(2)

        assertTrue(task.timerDuration().seconds >= 3.999 || task.timerDuration().seconds <= 4.001)

        task.stopTimer()
        assertTrue(task.timerIsStopped())
        assertFalse(task.timerIsRunning())
        assertFalse(task.timerIsPaused())

        assertEquals(0, task.timerDuration().seconds)

    }

    @DisplayName("Task Duration Constraint without Timer")
    @Test
    fun testTaskDurationConstraintWithoutTimer() {
        val task = testTask()
                .setDurationConstraintValue(Duration.ofSeconds(2))
        assertTrue(task.timerIsStopped())
        assertFalse(task.timerIsRunning())

        sleep(4)

        assertFalse((task.duration as Constraint).isMet)
        assertTrue(task.getAllUnmetAndShowingConstraints().isNotEmpty())
        assertThrows(TaskStateException::class.java, { task.kill() })

    }

    @DisplayName("Task Duration reset with Timer stop")
    @Test
    fun testTaskDurationReset() {
        val task = testTask()
                .setDurationConstraintValue(Duration.ofSeconds(2))

        sleep(2)

        assertEquals(Duration.ofSeconds(2), task.getDurationLeft())

        task.startTimer()

        sleep(1)

        task.stopTimer()

        assertEquals(Duration.ofSeconds(2), task.getDurationLeft())
    }

    @DisplayName("Task start Duration when SLEEPING")
    @Test
    fun testTaskStartDurationWhenSLEEPING() {
        val task = testTask()
                .setTimeConstraintValue(today.plusDays(1).atTime(16, 0))
                .setDurationConstraintValue(Duration.ofSeconds(2))

        assertThrows(IllegalStateException::class.java, { task.startTimer() })

        sleep(4)

        assertEquals(Duration.ofSeconds(2), task.getDurationLeft())
    }

    @DisplayName("Task start Duration when KILLED")
    @Test
    fun testTaskStartDurationWhenKILLED() {
        val task = testTask()

        task.kill()

        task.setDurationConstraintValue(Duration.ofSeconds(2))

        assertThrows(IllegalStateException::class.java, { task.startTimer() })

        sleep(4)

        assertEquals(Duration.ofSeconds(2), task.getDurationLeft())
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Task start Duration when FAILED")
    @Test
    fun testTaskStartDurationWhenFAILED() {
        val task = testTask()
        task.isFailable = true

        task.fail()

        task.setDurationConstraintValue(Duration.ofSeconds(2))

        assertThrows(IllegalStateException::class.java, { task.startTimer() })

        sleep(4)

        assertEquals(Duration.ofSeconds(2), task.getDurationLeft())
        assertEquals(TaskState.FAILED, task.state)
    }

}