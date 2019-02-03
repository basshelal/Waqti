package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.FinalSince
import uk.whitecrescent.waqti.WaqtiVersion
import uk.whitecrescent.waqti.after
import uk.whitecrescent.waqti.constraintProperty
import uk.whitecrescent.waqti.hiddenProperty
import uk.whitecrescent.waqti.millis
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.model.task.MET
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Properties
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskException
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.mustBe
import uk.whitecrescent.waqti.mustBeEmpty
import uk.whitecrescent.waqti.mustEqual
import uk.whitecrescent.waqti.mustHaveSizeOf
import uk.whitecrescent.waqti.mustNotThrow
import uk.whitecrescent.waqti.mustThrow
import uk.whitecrescent.waqti.now
import uk.whitecrescent.waqti.on
import uk.whitecrescent.waqti.seconds
import uk.whitecrescent.waqti.simpleProperty
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTimeFuture
import uk.whitecrescent.waqti.testTimePast

/**
 *
 * @author Bassam Helal
 * @since 03-Feb-19
 */
@FinalSince(WaqtiVersion.FEB_2019)
@DisplayName("Time Tests")
class Time : BaseTaskTest() {

    @DisplayName("Time Default Values")
    @Test
    fun testTaskTimeDefaultValues() {
        on(task.time) {
            isConstrained mustBe false
            value mustEqual DEFAULT_TIME
            isVisible mustBe false
            this mustEqual DEFAULT_TIME_PROPERTY
        }

        DEFAULT_TIME_PROPERTY mustEqual hiddenProperty(DEFAULT_TIME)
    }

    @DisplayName("Set Time Property")
    @Test
    fun testTaskSetTimeProperty() {
        task.setTimeProperty(simpleProperty(testTimePast))

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimePast
            isConstrained mustBe false
            isMet mustBe false
            this mustEqual simpleProperty(testTimePast)
        }

        after({ task.hideTime() }) {
            task.time mustEqual DEFAULT_TIME_PROPERTY
        }
    }

    @DisplayName("Set Time Property Value")
    @Test
    fun testTaskSetTimePropertyValue() {
        task.setTimePropertyValue(testTimePast)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimePast
            isConstrained mustBe false
            isMet mustBe false
            this mustEqual simpleProperty(testTimePast)
        }

        after({ task.hideTime() }) {
            task.time mustEqual DEFAULT_TIME_PROPERTY
        }
    }

    @DisplayName("Set Time Constraint")
    @Test
    fun testTaskSetTimeConstraint() {
        task.setTimeProperty(constraintProperty(testTimeFuture))

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimeFuture
            isConstrained mustBe true
            isMet mustBe false
            this mustEqual constraintProperty(testTimeFuture)
        }

        ({ task.hideTime() }) mustThrow TaskException::class
    }

    @DisplayName("Set Time Constraint Value")
    @Test
    fun testTaskSetTimeConstraintValue() {
        task.setTimeConstraintValue(testTimeFuture)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimeFuture
            isConstrained mustBe true
            isMet mustBe false
            this mustEqual constraintProperty(testTimeFuture)
        }

        ({ task.hideTime() }) mustThrow TaskException::class
    }

    @DisplayName("Set Time Property before now")
    @Test
    fun testTaskSetTimePropertyBeforeNow() {
        task.setTimePropertyValue(testTimePast)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimePast
            isConstrained mustBe false
            isMet mustBe false
            this mustEqual simpleProperty(testTimePast)
        }

        on(task) {
            isFailable mustBe false
            state mustEqual TaskState.EXISTING
        }
    }

    @DisplayName("Set Time Property after now")
    @Test
    fun testTaskSetTimePropertyAfterNow() {
        task.setTimePropertyValue(testTimeFuture)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimeFuture
            isConstrained mustBe false
            isMet mustBe false
            this mustEqual simpleProperty(testTimeFuture)
        }

        on(task) {
            isFailable mustBe false
            state mustEqual TaskState.EXISTING
        }
    }

    @DisplayName("Set Time Constraint before now")
    @Test
    fun testTaskSetTimeConstraintBeforeNow() {
        val time = now - 1.seconds

        task.setTimeConstraintValue(time)

        on(task.time) {
            isVisible mustBe true
            value mustEqual time
            isConstrained mustBe true
            isMet mustBe true
            this mustEqual Property(SHOWING, time, CONSTRAINED, MET)
        }

        on(task) {
            isFailable mustBe false
            state mustEqual TaskState.EXISTING
        }

        after({ sleep(1.seconds) }) {
            task.state mustEqual TaskState.EXISTING
        }
    }

    @DisplayName("Set Time Constraint after now")
    @Test
    fun testTaskSetTimeConstraintAfterNow() {
        val time = now + 500.millis
        task.setTimeConstraintValue(time)

        on(task.time) {
            isVisible mustBe true
            value mustEqual time
            isConstrained mustBe true
            isMet mustBe false
            this mustEqual constraintProperty(time)
        }

        on(task) {
            isFailable mustBe true
            state mustEqual TaskState.SLEEPING
            ({ hideTime() }) mustThrow TaskException::class
        }

        after({ sleep(1.seconds) }) {
            task.state mustEqual TaskState.EXISTING
            on(task.time) {
                isVisible mustBe true
                value mustEqual time
                isConstrained mustBe true
                isMet mustBe true
                this mustEqual Property(SHOWING, time, CONSTRAINED, MET)
            }
            ({ task.hideTime() }) mustNotThrow TaskException::class
        }

    }

    @DisplayName("Time Un-constraining on Constraint before now")
    @Test
    fun testTaskTimeUnConstrainingBeforeNow() {
        task.setTimeConstraintValue(testTimePast)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimePast
            isConstrained mustBe true
            isMet mustBe true
            this mustEqual Property(SHOWING, testTimePast, CONSTRAINED, MET)
        }

        on(task) {
            state mustEqual TaskState.EXISTING
            isFailable mustBe false
            allUnmetAndShowingConstraints.mustBeEmpty()
            allShowingConstraints mustHaveSizeOf 1
        }

        task.unConstrain(Properties.TIME)

        after({ sleep(1.seconds) }) {
            on(task.time) {
                isVisible mustBe true
                value mustEqual testTimePast
                isConstrained mustBe false
                isMet mustBe true // doesnt matter
                this mustEqual Property(SHOWING, testTimePast, NOT_CONSTRAINED, MET)
            }
            on(task) {
                state mustEqual TaskState.EXISTING
                isFailable mustBe false
                allUnmetAndShowingConstraints.mustBeEmpty()
            }
        }
    }

    @DisplayName("Time Un-constraining on Constraint after now")
    @Test
    fun testTaskTimeUnConstrainingAfterNow() {
        task.setTimeConstraintValue(testTimeFuture)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimeFuture
            isConstrained mustBe true
            isMet mustBe false
            this mustEqual Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET)
        }

        on(task) {
            state mustEqual TaskState.SLEEPING
            isFailable mustBe true
            allUnmetAndShowingConstraints mustHaveSizeOf 1
            { task.kill() } mustThrow TaskStateException::class
        }

        task.unConstrain(Properties.TIME)

        after({ sleep(1.seconds) }) {
            on(task.time) {
                isVisible mustBe true
                value mustEqual testTimeFuture
                isConstrained mustBe false
                isMet mustBe false // doesnt matter
                this mustEqual Property(SHOWING, testTimeFuture, NOT_CONSTRAINED, UNMET)
            }
            on(task) {
                state mustEqual TaskState.EXISTING
                isFailable mustBe false
                allUnmetAndShowingConstraints.mustBeEmpty()
            }
        }
    }

    @DisplayName("Time Constraint Re-Set")
    @Test
    fun testTaskTimeConstraintReSet() {
        task.setTimeConstraintValue(testTimeFuture)

        on(task.time) {
            isVisible mustBe true
            value mustEqual testTimeFuture
            isConstrained mustBe true
            isMet mustBe false
            this mustEqual Property(SHOWING, testTimeFuture, CONSTRAINED, UNMET)
        }

        on(task) {
            state mustEqual TaskState.SLEEPING
            isFailable mustBe true
            allUnmetAndShowingConstraints mustHaveSizeOf 1
            { task.kill() } mustThrow TaskStateException::class
        }

        val newTime = now + 500.millis
        task.setTimeConstraintValue(newTime)

        after({ sleep(1.seconds) }) {
            on(task.time) {
                isVisible mustBe true
                value mustEqual newTime
                isConstrained mustBe true
                isMet mustBe true
                this mustEqual Property(SHOWING, newTime, CONSTRAINED, MET)
            }
            on(task) {
                state mustEqual TaskState.EXISTING
                isFailable mustBe true
                allUnmetAndShowingConstraints.mustBeEmpty()
            }
        }
    }
}