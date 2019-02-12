package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.hiddenProperty
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_BEFORE_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_TASK_ID
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.mustBe
import uk.whitecrescent.waqti.mustEqual
import uk.whitecrescent.waqti.simpleProperty
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask

@DisplayName("Before Tests")
class Before : BaseTaskTest() {

    var beforeTask: Task = testTask

    override fun beforeEach() {
        super.beforeEach()

        beforeTask = testTask
    }

    @DisplayName("Before Default Values")
    @Test
    fun testTaskBeforeDefaultValues() {
        task.before.apply {
            isConstrained mustBe false
            value mustEqual DEFAULT_TASK_ID
            isVisible mustBe false
        }
    }

    @DisplayName("Set Before Property using setBeforeProperty")
    @Test
    fun testTaskSetBeforeProperty() {
        task.apply {
            setBeforeProperty(simpleProperty(beforeTask.id))

            before.isConstrained mustBe false
            before.value mustEqual beforeTask.id
            beforeTask mustEqual Caches.tasks[task.before.value]
            before.isVisible mustBe true

            hideBefore()
            before mustEqual hiddenProperty(DEFAULT_TASK_ID)
        }
    }

    @DisplayName("Set Before Property using setBeforeValue ID")
    @Test
    fun testTaskSetBeforeValueID() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforePropertyValue(
                        beforeTask.id
                )

        assertFalse(task.before.isConstrained)
        assertEquals(beforeTask.id, task.before.value)
        assertEquals(beforeTask, Caches.tasks.get(task.before.value))
        assertTrue(task.before.isVisible)

        task.hideBefore()
        assertEquals(Property(HIDDEN, DEFAULT_TASK_ID, NOT_CONSTRAINED, UNMET), task.before)
    }

    @DisplayName("Set Before Property using setBeforeValue Task")
    @Test
    fun testTaskSetBeforeValueTask() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforePropertyValue(
                        beforeTask
                )

        assertFalse(task.before.isConstrained)
        assertEquals(beforeTask.id, task.before.value)
        assertEquals(beforeTask, Caches.tasks.get(task.before.value))
        assertTrue(task.before.isVisible)

        task.hideBefore()
        assertEquals(Property(HIDDEN, DEFAULT_TASK_ID, NOT_CONSTRAINED, UNMET), task.before)
    }

    @DisplayName("Set Before Constraint using setBeforeProperty")
    @Test
    fun testTaskSetBeforePropertyWithConstraint() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforeProperty(
                        Property(SHOWING, beforeTask.id, CONSTRAINED, UNMET)
                )

        assertTrue(task.before.isConstrained)
        assertEquals(beforeTask.id, task.before.value)
        assertEquals(beforeTask, Caches.tasks.get(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before).isMet)
    }

    @DisplayName("Set Before Constraint using setBeforeConstraintValue ID")
    @Test
    fun testTaskSetBeforeConstraintValueID() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforeConstraintValue(beforeTask.id)

        assertTrue(task.before.isConstrained)
        assertEquals(beforeTask.id, task.before.value)
        assertEquals(beforeTask, Caches.tasks.get(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before).isMet)
    }

    @DisplayName("Set Before Constraint using setBeforeConstraintValue Task")
    @Test
    fun testTaskSetBeforeConstraintValueTask() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforeConstraintValue(beforeTask)

        assertTrue(task.before.isConstrained)
        assertEquals(beforeTask.id, task.before.value)
        assertEquals(beforeTask, Caches.tasks.get(task.before.value))
        assertTrue(task.before.isVisible)
        assertFalse((task.before).isMet)
    }

    @DisplayName("Set Before Property failable")
    @Test
    fun testTaskSetBeforePropertyFailable() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforePropertyValue(beforeTask)

        assertFalse(task.isFailable)
        assertFalse(task.before.isConstrained)
        assertEquals(beforeTask.id, task.before.value)
        assertTrue(task.before.isVisible)
    }

    @DisplayName("Kill with Before Property")
    @Test
    fun testTaskKillWithBeforeProperty() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforePropertyValue(beforeTask)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Kill with Before Constraint")
    @Test
    fun testTaskKillWithBeforeConstraint() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforeConstraintValue(beforeTask)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertFalse(task.state == TaskState.KILLED)

        Caches.tasks.get(beforeTask.id).kill()

        sleep(2)

        task.kill()

        assertTrue(task.state == TaskState.KILLED)

    }

    @DisplayName("Fail Before Constraint")
    @Test
    fun testTaskFailBeforeConstraint() {
        val beforeTask = Task("Before Task")
        beforeTask.isFailable = true
        val task = testTask
                .setBeforeConstraintValue(beforeTask)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertFalse(task.state == TaskState.KILLED)

        Caches.tasks.get(beforeTask.id).fail()

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

        Caches.tasks.get(beforeTask.id).kill()

        sleep(2)

        tasks.forEach { it.kill() }

    }

    @DisplayName("Before Un-constraining")
    @Test
    fun testTaskBeforeUnConstraining() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforeConstraintValue(beforeTask)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.allUnmetAndShowingConstraints.size == 1)
        task.setBeforeProperty(task.before.unConstrain())

        sleep(1)

        assertTrue(task.allUnmetAndShowingConstraints.isEmpty())
        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Before Constraint Re-Set")
    @Test
    fun testTaskBeforeConstraintReSet() {
        val beforeTask = Task("Before Task")
        val task = testTask
                .setBeforeConstraintValue(beforeTask)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.allUnmetAndShowingConstraints.size == 1)

        val newBeforeTask = Task("New Before Task")

        task.setBeforeConstraintValue(newBeforeTask)
        assertEquals(newBeforeTask.id, task.before.value)

        newBeforeTask.kill()

        sleep(2)

        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Before Hiding")
    @Test
    fun testBeforeHiding() {
        val before = Task("Before")

        val task = testTask
                .setBeforePropertyValue(before)
        assertEquals(before.id, task.before.value)

        task.hideBefore()
        assertEquals(DEFAULT_BEFORE_PROPERTY, task.before)

        task.setBeforeConstraintValue(before)
        assertEquals(before.id, task.before.value)
        assertThrows(IllegalStateException::class.java, { task.hideBefore() })
    }

}