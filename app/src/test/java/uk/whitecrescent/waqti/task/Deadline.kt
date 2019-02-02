package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_DEADLINE
import uk.whitecrescent.waqti.model.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.now
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask


// TODO: 09-Nov-18 Check this, unsure this is perfect
@DisplayName("Deadline Tests")
class Deadline : BaseTaskTest() {

    @DisplayName("Deadline Default Values")
    @Test
    fun testTaskDeadlineDefaultValues() {
        val task = testTask
        assertFalse(task.deadline.isConstrained)
        assertEquals(DEFAULT_DEADLINE, task.deadline.value)
        assertFalse(task.deadline.isVisible)
    }

    @DisplayName("Set Deadline Property using setDeadlineProperty")
    @Test
    fun testTaskSetDeadlineProperty() {
        val task = testTask
                .setDeadlineProperty(
                        Property(SHOWING, Time.of(1970, 1, 1, 1, 1), NOT_CONSTRAINED, UNMET)
                )

        assertFalse(task.deadline.isConstrained)
        assertEquals(Time.of(1970, 1, 1, 1, 1), task.deadline.value)
        assertTrue(task.deadline.isVisible)


        task.hideDeadline()
        assertEquals(Property(HIDDEN, DEFAULT_DEADLINE, NOT_CONSTRAINED, UNMET), task.deadline)
    }

    @DisplayName("Set Deadline Property using setDeadlinePropertyValue")
    @Test
    fun testTaskSetDeadlinePropertyValue() {
        val task = testTask
                .setDeadlinePropertyValue(
                        Time.of(1970, 1, 1, 1, 1)
                )

        assertFalse(task.deadline.isConstrained)
        assertEquals(Time.of(1970, 1, 1, 1, 1), task.deadline.value)
        assertTrue(task.deadline.isVisible)

        task.hideDeadline()
        assertEquals(Property(HIDDEN, DEFAULT_DEADLINE, NOT_CONSTRAINED, UNMET), task.deadline)
    }

    @DisplayName("Set Deadline Constraint using setDeadlineProperty")
    @Test
    fun testTaskSetDeadlinePropertyWithConstraint() {
        val task = testTask
                .setDeadlineProperty(
                        Property(SHOWING, Time.of(1970, 1, 1, 1, 1), CONSTRAINED, UNMET)
                )

        assertTrue(task.deadline.isConstrained)
        assertEquals(Time.of(1970, 1, 1, 1, 1), task.deadline.value)
        assertTrue(task.deadline.isVisible)
        assertTrue(task.deadline.isMet)
    }

    @DisplayName("Set Deadline Constraint using setDeadlineConstraintValue")
    @Test
    fun testTaskSetDeadlineConstraintValue() {
        val task = testTask
                .setDeadlineConstraintValue(Time.of(1970, 1, 1, 1, 1))

        assertTrue(task.deadline.isConstrained)
        assertEquals(Time.of(1970, 1, 1, 1, 1), task.deadline.value)
        assertTrue(task.deadline.isVisible)
        assertTrue(task.deadline.isMet)
    }

    @DisplayName("Set Deadline Property failable")
    @Test
    fun testTaskSetDeadlinePropertyFailable() {
        val deadline = now.plusSeconds(10)
        val task = testTask
                .setDeadlinePropertyValue(deadline)

        assertFalse(task.isFailable)
        assertFalse(task.deadline.isConstrained)
        assertEquals(deadline, task.deadline.value)
        assertTrue(task.deadline.isVisible)
    }

    @DisplayName("Set Deadline Constraint failable")
    @Test
    fun testTaskSetDeadlineConstraintFailable() {
        val deadline = now.plusSeconds(2)
        val task = testTask
                .setDeadlineProperty(Property(SHOWING, deadline, CONSTRAINED, UNMET))


        assertTrue(task.isFailable)
        assertTrue(task.deadline.isConstrained)
        assertEquals(deadline, task.deadline.value)
        assertTrue(task.deadline.isVisible)
        assertFalse((task.deadline).isMet)

        sleep(4)

        assertEquals(TaskState.FAILED, task.state)
        assertTrue(task.deadline.isMet)
    }

    @DisplayName("Kill with deadline Constraint past")
    @Test
    fun testTaskKillDeadlineConstraintPast() {
        val deadline = now.plusSeconds(2)
        val task = testTask
                .setDeadlineProperty(Property(SHOWING, deadline, CONSTRAINED, UNMET))

        assertTrue(task.isFailable)
        assertTrue(task.deadline.isConstrained)
        assertEquals(deadline, task.deadline.value)
        assertTrue(task.deadline.isVisible)
        assertTrue(task.deadline.isMet)

        sleep(4)
        assertThrows(TaskStateException::class.java, { task.kill() })

        assertEquals(TaskState.FAILED, task.state)
        assertFalse((task.deadline).isMet)

    }

    @DisplayName("Kill with deadline Constraint later")
    @Test
    fun testTaskKillDeadlineConstraintLater() {
        val deadline = now.plusSeconds(5)
        val task = testTask
                .setDeadlineProperty(Property(SHOWING, deadline, CONSTRAINED, UNMET))

        assertTrue(task.isFailable)
        assertTrue(task.deadline.isConstrained)
        assertEquals(deadline, task.deadline.value)
        assertTrue(task.deadline.isVisible)
        assertFalse((task.deadline).isMet)

        sleep(3)
        task.kill()

        assertEquals(TaskState.KILLED, task.state)
        assertTrue((task.deadline).isMet)

        sleep(3)

        assertEquals(TaskState.KILLED, task.state)
        assertTrue(task.deadline.isMet)

    }

    @DisplayName("Set Deadline Constraint on many Tasks")
    @Test
    fun testTaskSetDeadlineConstraintOnManyTasks() {
        val deadline = now.plusSeconds(5)
        val tasks = getTasks(100)
        tasks.forEach { it.setDeadlineConstraintValue(deadline) }

        sleep(2)

        tasks.forEach { it.kill() }

    }

    @DisplayName("Get Time Left until Deadline")
    @Test
    fun testTaskGetTimeLeftUntilDeadline() {
        val deadline = now.plusSeconds(3)
        val task = testTask.setDeadlineConstraintValue(deadline)

        sleep(2)

        assertTrue(Math.abs(
                Duration.between(now, deadline).seconds - task.getTimeUntilDeadline().seconds) <= 1
        )
    }

    @DisplayName("Get Duration Left Default")
    @Test
    fun testTaskGetDurationLeftDefault() {
        val task = testTask
        assertThrows(IllegalStateException::class.java, { task.getTimeUntilDeadline() })
    }

    @DisplayName("Deadline Un-constraining")
    @Test
    fun testTaskDeadlineUnConstraining() {
        val task = testTask
                .setDeadlineConstraintValue(Time.from(now.plusDays(7)))

        sleep(1)
        task.setDeadlineProperty((task.deadline).unConstrain())

        sleep(2)

        assertFalse(task.isFailable)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Deadline Constraint Re-Set")
    @Test
    fun testTaskDeadlineConstraintReSet() {
        val task = testTask
                .setDeadlineConstraintValue(Time.from(now.plusDays(7)))

        sleep(1)

        val newDeadline = Time.from(now.plusSeconds(1))

        task.setDeadlineConstraintValue(newDeadline)
        assertEquals(newDeadline, task.deadline.value)

        sleep(3)

        assertEquals(TaskState.FAILED, task.state)
    }

    @DisplayName("Deadline Hiding")
    @Test
    fun testDeadlineHiding() {
        val deadline = Time.from(now.plusDays(7))

        val task = testTask
                .setDeadlinePropertyValue(deadline)
        assertEquals(deadline, task.deadline.value)

        task.hideDeadline()
        assertEquals(DEFAULT_DEADLINE_PROPERTY, task.deadline)

        task.setDeadlineConstraintValue(deadline)
        assertEquals(deadline, task.deadline.value)
        assertThrows(IllegalStateException::class.java, { task.hideDeadline() })
    }

}