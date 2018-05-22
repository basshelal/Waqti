package uk.whitecrescent.waqti.tests.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.Cache
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.task.Constraint
import uk.whitecrescent.waqti.task.DEFAULT_BEFORE_PROPERTY
import uk.whitecrescent.waqti.task.DEFAULT_TASK_ID
import uk.whitecrescent.waqti.task.HIDDEN
import uk.whitecrescent.waqti.task.Property
import uk.whitecrescent.waqti.task.SHOWING
import uk.whitecrescent.waqti.task.Task
import uk.whitecrescent.waqti.task.TaskState
import uk.whitecrescent.waqti.task.TaskStateException
import uk.whitecrescent.waqti.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Before Tests")
class Before {

    @DisplayName("Before Default Values")
    @Test
    fun testTaskBeforeDefaultValues() {
        val task = testTask()
        assertFalse(task.before is Constraint)
        assertEquals(DEFAULT_TASK_ID, task.before.value)
        assertFalse(task.before.isVisible)
    }

    @DisplayName("Set Before Property using setBeforeProperty")
    @Test
    fun testTaskSetBeforeProperty() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeProperty(
                        Property(SHOWING, beforeTask.taskID)
                )

        assertFalse(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)


        task.hideBefore()
        assertEquals(Property(HIDDEN, DEFAULT_TASK_ID), task.before)
    }

    @DisplayName("Set Before Property using setBeforeValue ID")
    @Test
    fun testTaskSetBeforeValueID() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforePropertyValue(
                        beforeTask.taskID
                )

        assertFalse(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)

        task.hideBefore()
        assertEquals(Property(HIDDEN, DEFAULT_TASK_ID), task.before)
    }

    @DisplayName("Set Before Property using setBeforeValue Task")
    @Test
    fun testTaskSetBeforeValueTask() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforePropertyValue(
                        beforeTask
                )

        assertFalse(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)

        task.hideBefore()
        assertEquals(Property(HIDDEN, DEFAULT_TASK_ID), task.before)
    }

    @DisplayName("Set Before Constraint using setBeforeProperty")
    @Test
    fun testTaskSetBeforePropertyWithConstraint() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeProperty(
                        Constraint(SHOWING, beforeTask.taskID, UNMET)
                )

        assertTrue(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before as Constraint).isMet)
    }

    @DisplayName("Set Before Constraint using setBeforeConstraint")
    @Test
    fun testTaskSetBeforeConstraint() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraint(
                        Constraint(SHOWING, beforeTask.taskID, UNMET)
                )

        assertTrue(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before as Constraint).isMet)
    }

    @DisplayName("Set Before Constraint using setBeforeConstraintValue ID")
    @Test
    fun testTaskSetBeforeConstraintValueID() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraintValue(beforeTask.taskID)

        assertTrue(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before as Constraint).isMet)
    }

    @DisplayName("Set Before Constraint using setBeforeConstraintValue Task")
    @Test
    fun testTaskSetBeforeConstraintValueTask() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraintValue(beforeTask)

        assertTrue(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertEquals(beforeTask, Cache.getTask(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before as Constraint).isMet)
    }

    @DisplayName("Set Before Property failable")
    @Test
    fun testTaskSetBeforePropertyFailable() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforePropertyValue(beforeTask)

        assertFalse(task.isFailable)
        assertFalse(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertTrue(task.before.isVisible)
    }

    @DisplayName("Set Before Constraint failable")
    @Test
    fun testTaskSetBeforeConstraintFailable() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraint(
                        Constraint(SHOWING, beforeTask.taskID, UNMET)
                )

        assertTrue(task.isFailable)
        assertTrue(task.before is Constraint)
        assertEquals(beforeTask.taskID, task.before.value)
        assertTrue(task.before.isVisible)
        assertFalse((task.before as Constraint).isMet)
    }

    @DisplayName("Kill with Before Property")
    @Test
    fun testTaskKillWithBeforeProperty() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforePropertyValue(beforeTask)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Kill with Before Constraint")
    @Test
    fun testTaskKillWithBeforeConstraint() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraintValue(beforeTask)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertFalse(task.state == TaskState.KILLED)

        Cache.getTask(beforeTask.taskID).kill()

        sleep(2)

        task.kill()

        assertTrue(task.state == TaskState.KILLED)

    }

    @DisplayName("Fail Before Constraint")
    @Test
    fun testTaskFailBeforeConstraint() {
        val beforeTask = Task("Before Task")
        beforeTask.isFailable = true
        val task = testTask()
                .setBeforeConstraintValue(beforeTask)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertFalse(task.state == TaskState.KILLED)

        Cache.getTask(beforeTask.taskID).fail()

        sleep(2)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertTrue(task.state == TaskState.FAILED)

    }

    @DisplayName("Set Before Constraint on many Tasks")
    @Test
    fun testTaskSetBeforeConstraintOnManyTasks() {
        val beforeTask = Task("Before Task")
        val tasks = getTasks(1000)
        tasks.forEach { it.setBeforeConstraintValue(beforeTask) }

        assertThrows(TaskStateException::class.java, { tasks.forEach { it.kill() } })

        Cache.getTask(beforeTask.taskID).kill()

        sleep(2)

        tasks.forEach { it.kill() }

    }

    @DisplayName("Before Un-constraining")
    @Test
    fun testTaskBeforeUnConstraining() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraintValue(beforeTask)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setBeforeProperty((task.before as Constraint).toProperty())

        sleep(1)

        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Before Constraint Re-Set")
    @Test
    fun testTaskBeforeConstraintReSet() {
        val beforeTask = Task("Before Task")
        val task = testTask()
                .setBeforeConstraintValue(beforeTask)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        val newBeforeTask = Task("New Before Task")

        task.setBeforeConstraintValue(newBeforeTask)
        assertEquals(newBeforeTask.taskID, task.before.value)

        newBeforeTask.kill()

        sleep(2)

        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Before Hiding")
    @Test
    fun testBeforeHiding() {
        val before = Task("Before")

        val task = testTask()
                .setBeforePropertyValue(before)
        assertEquals(before.taskID, task.before.value)

        task.hideBefore()
        assertEquals(DEFAULT_BEFORE_PROPERTY, task.before)

        task.setBeforeConstraintValue(before)
        assertEquals(before.taskID, task.before.value)
        assertThrows(IllegalStateException::class.java, { task.hideBefore() })
    }

}