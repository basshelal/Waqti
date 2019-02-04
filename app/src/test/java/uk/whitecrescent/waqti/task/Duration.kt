package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.FinalSince
import uk.whitecrescent.waqti.WaqtiVersion
import uk.whitecrescent.waqti.after
import uk.whitecrescent.waqti.at
import uk.whitecrescent.waqti.constraintProperty
import uk.whitecrescent.waqti.hiddenProperty
import uk.whitecrescent.waqti.hours
import uk.whitecrescent.waqti.millis
import uk.whitecrescent.waqti.minutes
import uk.whitecrescent.waqti.model.task.CannotHidePropertyException
import uk.whitecrescent.waqti.model.task.DEFAULT_DURATION
import uk.whitecrescent.waqti.model.task.DEFAULT_DURATION_PROPERTY
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Properties
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskException
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.TimeUnit
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.mustBe
import uk.whitecrescent.waqti.mustBeEmpty
import uk.whitecrescent.waqti.mustBeIn
import uk.whitecrescent.waqti.mustBeLessThan
import uk.whitecrescent.waqti.mustEqual
import uk.whitecrescent.waqti.mustHaveSizeOf
import uk.whitecrescent.waqti.mustNotBeEmpty
import uk.whitecrescent.waqti.mustNotThrow
import uk.whitecrescent.waqti.mustThrow
import uk.whitecrescent.waqti.on
import uk.whitecrescent.waqti.seconds
import uk.whitecrescent.waqti.simpleProperty
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testDuration
import uk.whitecrescent.waqti.testTimeUnit
import uk.whitecrescent.waqti.tomorrow

/**
 *
 * @author Bassam Helal
 * @since 04-Feb-19
 */
@FinalSince(WaqtiVersion.FEB_2019)
@DisplayName("Duration Tests")
class Duration : BaseTaskTest() {

    var timeUnit: TimeUnit = testTimeUnit
    var duration: Duration = testDuration

    override fun beforeEach() {
        super.beforeEach()

        timeUnit = testTimeUnit
        duration = testDuration
    }

    @DisplayName("TimeUnits")
    @Test
    fun testTimeUnits() {
        timeUnit.duration = 25.minutes

        timeUnit * 2 mustEqual 50.minutes

        timeUnit.duration = 30.minutes

        timeUnit * 4 mustEqual 2.hours
    }

    @DisplayName("Duration Default Values")
    @Test
    fun testTaskDurationDefaultValues() {

        on(task.duration) {
            isVisible mustBe false
            value mustEqual DEFAULT_DURATION
            isConstrained mustBe false
            isMet mustBe false
            this mustEqual DEFAULT_DURATION_PROPERTY
        }
        DEFAULT_DURATION_PROPERTY mustEqual hiddenProperty(DEFAULT_DURATION)
    }

    @DisplayName("Set Duration Property")
    @Test
    fun testTaskSetDurationProperty() {
        task.setDurationProperty(Property(SHOWING, duration, NOT_CONSTRAINED, UNMET))

        on(task.duration) {
            isVisible mustBe true
            value mustEqual duration
            isConstrained mustBe false
            isMet mustBe false
        }

        after({ task.hideDuration() }) {
            task.duration mustEqual DEFAULT_DURATION_PROPERTY
        }
    }

    @DisplayName("Set Duration Property Value")
    @Test
    fun testTaskSetDurationPropertyValue() {
        task.setDurationPropertyValue(duration)

        on(task.duration) {
            isVisible mustBe true
            value mustEqual duration
            isConstrained mustBe false
            isMet mustBe false
        }

        after({ task.hideDuration() }) {
            task.duration mustEqual DEFAULT_DURATION_PROPERTY
        }
    }

    @DisplayName("Set Duration Constraint")
    @Test
    fun testTaskSetDurationPropertyConstraint() {
        task.setDurationProperty(constraintProperty(duration))

        on(task.duration) {
            isVisible mustBe true
            value mustEqual duration
            isConstrained mustBe true
            isMet mustBe false
        }

        ({ task.hideDuration() }) mustThrow CannotHidePropertyException::class
    }

    @DisplayName("Set Duration Constraint Value")
    @Test
    fun testTaskSetDurationConstraintValue() {
        task.setDurationConstraintValue(duration)

        on(task.duration) {
            isVisible mustBe true
            value mustEqual duration
            isConstrained mustBe true
            isMet mustBe false
        }

        ({ task.hideDuration() }) mustThrow CannotHidePropertyException::class
    }

    @DisplayName("Set Duration Property TimeUnits")
    @Test
    fun testTaskSetDurationPropertyTimeUnits() {

        task.setDurationPropertyTimeUnits(simpleProperty(timeUnit), 6)

        on(task.duration) {
            isVisible mustBe true
            value mustEqual timeUnit * 6
            isConstrained mustBe false
            isMet mustBe false
        }

        after({ task.hideDuration() }) {
            task.duration mustEqual DEFAULT_DURATION_PROPERTY
        }
    }

    @DisplayName("Set Duration Property Value TimeUnits")
    @Test
    fun testTaskSetDurationValueTimeUnits() {
        task.setDurationPropertyTimeUnitsValue(timeUnit, 6)

        on(task.duration) {
            isVisible mustBe true
            value mustEqual timeUnit * 6
            isConstrained mustBe false
            isMet mustBe false
        }

        after({ task.hideDuration() }) {
            task.duration mustEqual DEFAULT_DURATION_PROPERTY
        }
    }

    @DisplayName("Set Duration Constraint TimeUnits")
    @Test
    fun testTaskSetDurationConstraintTimeUnits() {
        task.setDurationPropertyTimeUnits(constraintProperty(timeUnit), 6)

        on(task.duration) {
            isVisible mustBe true
            value mustEqual timeUnit * 6
            isConstrained mustBe true
            isMet mustBe false
        }

        ({ task.hideDuration() }) mustThrow CannotHidePropertyException::class
    }

    @DisplayName("Set Duration Constraint Value TimeUnits")
    @Test
    fun testTaskSetDurationConstraintValueTimeUnits() {
        task.setDurationConstraintTimeUnitsValue(timeUnit, 6)

        on(task.duration) {
            isVisible mustBe true
            value mustEqual timeUnit * 6
            isConstrained mustBe true
            isMet mustBe false
        }

        ({ task.hideDuration() }) mustThrow CannotHidePropertyException::class
    }

    @DisplayName("Set Duration Property failable")
    @Test
    fun testTaskSetDurationPropertyFailable() {
        task.setDurationPropertyValue(duration)

        task.isFailable mustBe false
    }

    @DisplayName("Set Duration Constraint failable")
    @Test
    fun testTaskSetDurationConstraintFailable() {
        duration = 500.millis
        task.setDurationConstraintValue(duration)

        on(task) {
            isFailable mustBe true
            { kill() } mustThrow TaskStateException::class
        }

        after({
            task.startTimer()
            sleep(1.seconds)
        }) {
            on(task) {
                { kill() } mustNotThrow TaskStateException::class
                state mustEqual TaskState.KILLED
                duration.isMet mustBe true
            }
        }
    }

    @DisplayName("Get Duration Left Default")
    @Test
    fun testTaskGetDurationLeftDefault() {
        { task.durationLeft } mustThrow TaskException::class
    }

    @DisplayName("Duration Un-constraining while timer running")
    @Test
    fun testTaskDurationUnConstraining() {
        task.setDurationConstraintValue(duration)

        after({
            task.startTimer()
            sleep(1.seconds)
        }) {
            on(task) {
                { kill() } mustThrow TaskStateException::class
                allUnmetAndShowingConstraints mustHaveSizeOf 1
            }
        }

        after({
            task.unConstrain(Properties.DURATION)
        }) {
            task.timerIsRunning mustBe true
            task.duration.isConstrained mustBe false
        }


        after({
            task.startTimer()
            sleep(1.seconds)
        }) {
            task.allUnmetAndShowingConstraints.mustBeEmpty()
        }

        after({ task.kill() }) {
            task.state mustEqual TaskState.KILLED
        }
    }

    @DisplayName("Duration Constraint Re-Set")
    @Test
    fun testTaskDurationConstraintReSet() {
        task.setDurationConstraintValue(duration)

        after({
            task.startTimer()
        }) {
            on(task) {
                { kill() } mustThrow TaskStateException::class
                allUnmetAndShowingConstraints mustHaveSizeOf 1
            }
        }

        duration = 500.millis

        after({ task.stopTimer().setDurationConstraintValue(duration) }) {
            task.duration.value mustEqual duration
        }

        after({
            task.startTimer()
            sleep(1.seconds)
            task.kill()
        }) {
            task.state mustEqual TaskState.KILLED
        }
    }

    @DisplayName("Duration Hiding")
    @Test
    fun testDurationHiding() {
        task.setDurationPropertyValue(duration)

        after({ task.hideDuration() }) {
            task.duration mustEqual DEFAULT_DURATION_PROPERTY
        }

        after({ task.setDurationConstraintValue(duration) }) {
            task.duration.value mustEqual duration
            { task.hideDuration() } mustThrow CannotHidePropertyException::class
        }
    }

    @DisplayName("Task Timer Independently")
    @Test
    fun testTaskTimer() {
        on(task) {
            after({ startTimer() }) {
                timerIsRunning mustBe true
                timerIsPaused mustBe false
                timerIsStopped mustBe false
            }

            after({
                sleep(1.seconds)
                pauseTimer()
            }) {
                timerIsRunning mustBe false
                timerIsPaused mustBe true
                timerIsStopped mustBe false
                timerDuration.millis mustBeIn 900L..1100L
            }

            after({
                startTimer()
                sleep(1.seconds)
            }) {
                timerDuration.millis mustBeIn 1900L..2100L
            }

            after({
                stopTimer()
                sleep(1.seconds)
            }) {
                timerIsRunning mustBe false
                timerIsPaused mustBe false
                timerIsStopped mustBe true
                timerDuration mustEqual Duration.ZERO
            }
        }
    }

    @DisplayName("Task Duration Constraint without Timer")
    @Test
    fun testTaskDurationConstraintWithoutTimer() {
        task.setDurationConstraintValue(500.millis)

        on(task) {
            timerIsRunning mustBe false
            timerIsPaused mustBe false
            timerIsStopped mustBe true

            after({ sleep(1.seconds) }) {
                duration.isMet mustBe false
                allUnmetAndShowingConstraints.mustNotBeEmpty()
                ({ kill() }) mustThrow TaskStateException::class
            }
        }
    }

    @DisplayName("Task Duration reset with Timer stop")
    @Test
    fun testTaskDurationReset() {
        task.setDurationConstraintValue(2.seconds)

        on(task) {
            after({ sleep(1.seconds) }) {
                durationLeft mustEqual 2.seconds
            }

            after({
                startTimer()
                sleep(1.seconds)
            }) {
                durationLeft mustBeLessThan 2.seconds
            }

            after({ stopTimer() }) {
                durationLeft mustEqual 2.seconds
            }
        }
    }

    @DisplayName("Task start Timer when SLEEPING")
    @Test
    fun testTaskStartDurationWhenSLEEPING() {
        task.setTimeConstraintValue(tomorrow at 11)
                .setDurationConstraintValue(500.millis)

        on(task) {
            state mustEqual TaskState.SLEEPING
            { startTimer() } mustThrow TaskStateException::class

            after({
                unConstrain(Properties.TIME)
                sleep(1.seconds)
            }) {
                state mustEqual TaskState.EXISTING
                { startTimer() } mustNotThrow TaskStateException::class
            }
        }
    }

    @DisplayName("Task start Duration Constraint and Timer when KILLED")
    @Test
    fun testTaskStartDurationWhenKILLED() {

        on(task) {
            after({
                kill()
                setDurationConstraintValue(500.millis)
            }) {
                state mustEqual TaskState.KILLED
                { startTimer() } mustThrow TaskStateException::class
            }
            after({ sleep(1.seconds) }) {
                durationLeft mustEqual 500.millis
            }
        }
    }

    @DisplayName("Task start Duration Constraint and Timer when FAILED")
    @Test
    fun testTaskStartDurationWhenFAILED() {
        on(task) {
            isFailable = true
            after({
                fail()
                setDurationConstraintValue(500.millis)
            }) {
                state mustEqual TaskState.FAILED
                { startTimer() } mustThrow TaskStateException::class
            }

            after({ sleep(1.seconds) }) {
                durationLeft mustEqual 500.millis
            }
        }
    }

}