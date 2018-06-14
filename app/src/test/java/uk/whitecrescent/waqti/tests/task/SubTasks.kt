package uk.whitecrescent.waqti.tests.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.Cache
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.taskIDs
import uk.whitecrescent.waqti.model.tasks
import uk.whitecrescent.waqti.model.toArrayList
import uk.whitecrescent.waqti.task.Constraint
import uk.whitecrescent.waqti.task.DEFAULT_SUB_TASKS
import uk.whitecrescent.waqti.task.DEFAULT_SUB_TASKS_PROPERTY
import uk.whitecrescent.waqti.task.HIDDEN
import uk.whitecrescent.waqti.task.Property
import uk.whitecrescent.waqti.task.SHOWING
import uk.whitecrescent.waqti.task.Task
import uk.whitecrescent.waqti.task.TaskState
import uk.whitecrescent.waqti.task.TaskStateException
import uk.whitecrescent.waqti.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("SubTasks Tests")
class SubTasks {

    @DisplayName("SubTasks Default Values")
    @Test
    fun testTaskSubTasksDefaultValues() {
        val task = testTask()
        assertFalse(task.subTasks is Constraint)
        assertEquals(DEFAULT_SUB_TASKS, task.subTasks.value)
        assertFalse(task.subTasks.isVisible)
    }

    @DisplayName("Set SubTasks Property using setSubTasksProperty")
    @Test
    fun testTaskSetSubTasksProperty() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()
        val task = testTask()
                .setSubTasksProperty(
                        Property(SHOWING, subTasksIDs.toArrayList)
                )

        assertFalse(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)


        task.hideSubTasks()
        assertEquals(Property(HIDDEN, DEFAULT_SUB_TASKS), task.subTasks)
    }

    @DisplayName("Set SubTasks Property using setSubTasksValue")
    @Test
    fun testTaskSetSubTasksValue() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()
        val task = testTask()
                .setSubTasksPropertyValue(
                        subTasksIDs.toArrayList
                )

        assertFalse(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)

        task.hideSubTasks()
        assertEquals(Property(HIDDEN, DEFAULT_SUB_TASKS), task.subTasks)
    }

    @DisplayName("Set SubTasks Constraint using setSubTasksProperty")
    @Test
    fun testTaskSetSubTasksPropertyWithConstraint() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksProperty(
                        Constraint(SHOWING, subTasksIDs.toArrayList, UNMET)
                )

        assertTrue(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse((task.subTasks as Constraint).isMet)
    }

    @DisplayName("Set SubTasks Constraint using setSubTasksConstraint")
    @Test
    fun testTaskSetSubTasksConstraint() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksConstraint(
                        Constraint(SHOWING, subTasksIDs.toArrayList, UNMET)
                )

        assertTrue(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse((task.subTasks as Constraint).isMet)
    }

    @DisplayName("Set SubTasks Constraint using setSubTasksConstraintValue Task")
    @Test
    fun testTaskSetSubTasksConstraintValueTask() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksConstraintValue(subTasksIDs.toArrayList)

        assertTrue(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse((task.subTasks as Constraint).isMet)
    }

    @DisplayName("Set SubTasks Property failable")
    @Test
    fun testTaskSetSubTasksPropertyFailable() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksPropertyValue(subTasksIDs.toArrayList)

        assertFalse(task.isFailable)
        assertFalse(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
    }

    @DisplayName("Set SubTasks Constraint failable")
    @Test
    fun testTaskSetSubTasksConstraintFailable() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksConstraint(
                        Constraint(SHOWING, subTasksIDs.toArrayList, UNMET)
                )

        assertTrue(task.isFailable)
        assertTrue(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse((task.subTasks as Constraint).isMet)
    }

    @DisplayName("Add SubTasks")
    @Test
    fun testTaskAddSubTasks() {
        val task = testTask().addSubTasks(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        val subTasksIDs = task.getSubTasksList().taskIDs()

        assertFalse(task.subTasks is Constraint)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)

        task.hideSubTasks()
        assertEquals(Property(HIDDEN, DEFAULT_SUB_TASKS), task.subTasks)
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, task.subTasks)
    }

    @DisplayName("Get SubTasks List")
    @Test
    fun testTaskGetSubTasksList() {
        val task = testTask().addSubTasks(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        val subTasksIDs = task.getSubTasksList().taskIDs()
        assertEquals(subTasksIDs, task.getSubTasksIDsList())
        assertEquals(subTasksIDs.tasks, task.getSubTasksList())

    }

    @DisplayName("Kill with SubTasks Property")
    @Test
    fun testTaskKillWithSubTasksProperty() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksPropertyValue(subTasksIDs.toArrayList)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Kill with SubTasks Constraint")
    @Test
    fun testTaskKillWithSubTasksConstraint() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val task = testTask()
                .setSubTasksConstraintValue(subTasksIDs.toArrayList)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertFalse(task.state == TaskState.KILLED)

        Cache.getTask(subTasksIDs[0]).kill()
        Cache.getTask(subTasksIDs[1]).kill()

        assertThrows(TaskStateException::class.java, { task.kill() })

        Cache.getTask(subTasksIDs[2]).kill()

        sleep(2)

        task.kill()

        assertTrue(task.state == TaskState.KILLED)

    }

    @DisplayName("Fail SubTasks Constraint")
    @Test
    fun testTaskFailSubTasksConstraint() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        subTasks.forEach { it.isFailable = true }

        val task = testTask()
                .setSubTasksConstraintValue(subTasksIDs.toArrayList)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertFalse(task.state == TaskState.KILLED)

        Cache.getTask(subTasksIDs[0]).fail()
        Cache.getTask(subTasksIDs[1]).kill()
        Cache.getTask(subTasksIDs[2]).kill()

        sleep(2)

        assertThrows(TaskStateException::class.java, { task.kill() })

        assertTrue(task.state == TaskState.FAILED)

    }

    @DisplayName("SubTasks Constraint on many Tasks")
    @Test
    fun testTaskSetSubTasksConstraintOnManyTasks() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.taskIDs()

        val tasks = getTasks(1000)
        tasks.forEach { it.setSubTasksConstraintValue(subTasksIDs.toArrayList) }

        assertThrows(TaskStateException::class.java, { tasks.forEach { it.kill() } })

        Cache.getTask(subTasksIDs[0]).kill()
        Cache.getTask(subTasksIDs[1]).kill()
        Cache.getTask(subTasksIDs[2]).kill()

        sleep(2)

        tasks.forEach { it.kill() }

    }

    @DisplayName("SubTasks depth")
    @Test
    fun testTaskSubTasksDepth() {
        val root = Task("Root")
        val level1 = Task("Level1")
        val level2 = Task("Level2")
        val level3 = Task("Level3")
        val level4 = Task("Level4")

        level3.setSubTasksConstraintValue(arrayListOf(level4.taskID))
        level2.setSubTasksConstraintValue(arrayListOf(level3.taskID))
        level1.setSubTasksConstraintValue(arrayListOf(level2.taskID))
        root.setSubTasksConstraintValue(arrayListOf(level1.taskID))

        assertThrows(TaskStateException::class.java, { root.kill() })
        assertThrows(TaskStateException::class.java, { level1.kill() })
        assertThrows(TaskStateException::class.java, { level2.kill() })
        assertThrows(TaskStateException::class.java, { level3.kill() })

        assertEquals(1, root.subTasks.value.size)
        assertEquals(1, level1.subTasks.value.size)
        assertEquals(1, level2.subTasks.value.size)
        assertEquals(1, level3.subTasks.value.size)
        assertEquals(0, level4.subTasks.value.size)

        val rootSubTasks = root.subTasks.value.tasks
        val level1SubTasks = rootSubTasks.first().subTasks.value.tasks
        val level2SubTasks = level1SubTasks.first().subTasks.value.tasks
        val level3SubTasks = level2SubTasks.first().subTasks.value.tasks
        val level4SubTasks = level3SubTasks.first().subTasks.value.tasks

        assertEquals("Level1", rootSubTasks.first().title)
        assertEquals("Level2", level1SubTasks.first().title)
        assertEquals("Level3", level2SubTasks.first().title)
        assertEquals("Level4", level3SubTasks.first().title)
        assertEquals(0, level4SubTasks.size)

        level4.kill()
        sleep(2)
        level3.kill()
        sleep(2)
        level2.kill()
        sleep(2)
        level1.kill()
        sleep(2)
        root.kill()

    }

    @DisplayName("Before Constraining in SubTasks")
    @Test
    fun testTaskBeforeConstrainingInSubTasks() {
        val subTask2SubTask = Task("SubTask2SubTask")

        val subTask0 = Task("SubTask0")
        val subTask1 = Task("SubTask1")

        val subTask2 = Task("SubTask2")
                .setSubTasksConstraintValue(arrayListOf(subTask2SubTask).taskIDs().toArrayList)
                .setBeforeConstraintValue(subTask1)

        val subTask3 = Task("SubTask3")
                .setBeforeConstraintValue(subTask2)

        assertThrows(TaskStateException::class.java, { subTask2.kill() })
        assertThrows(TaskStateException::class.java, { subTask3.kill() })

        val task = testTask()
                .setSubTasksConstraintValue(
                        arrayListOf(
                                subTask0,
                                subTask1,
                                subTask2,
                                subTask3

                        ).taskIDs().toArrayList
                )

        assertThrows(TaskStateException::class.java, { task.kill() })

        subTask0.kill()
        subTask1.kill()

        sleep(2)

        assertThrows(TaskStateException::class.java, { subTask2.kill() })

        subTask2SubTask.kill()

        sleep(2)

        subTask2.kill()

        assertThrows(TaskStateException::class.java, { task.kill() })

        sleep(2)

        subTask3.kill()

        sleep(2)

        task.kill()
    }

    @DisplayName("SubTasks Extra depth")
    @Test
    fun testTaskSubTasksExtraDepth() {

        Cache.clearTasks()

        val list = getTasks(500)

        list.forEachIndexed { index, task ->
            run {
                if (index != 499) task.setSubTasksConstraintValue(arrayListOf(list[index + 1].taskID))
            }
        }

        assertEquals(1, list[0].subTasks.value.size)

        list.minus(list.last()).reversed().forEach { assertTrue(it.subTasks.value.size == 1) }
        list.minus(list.last()).reversed().forEach { assertThrows(TaskStateException::class.java, { it.kill() }) }

        assertEquals(499, list[0].getSubTasksLevelsDepth())
        assertEquals(300, list[199].getSubTasksLevelsDepth())
        assertEquals(0, list[499].getSubTasksLevelsDepth())

        assertTrue(Cache.allTasks().size == 500)

        Cache.clearTasks()
    }

    @DisplayName("SubTasks varied depth")
    @Test
    fun testTaskSubTasksVariedDepth() {
        val level1A = Task("Level1A")
        val level1B = Task("Level1B")
        val level2A = Task("Level2A")
        val level2B = Task("Level2B")
        val level3A = Task("Level3A")
        val level4A = Task("Level4A")
        val root = Task("Root")

        assertEquals(0, root.getSubTasksLevelsDepth())

        level3A.setSubTasksConstraintValue(arrayListOf(level4A.taskID))
        level2A.setSubTasksConstraintValue(arrayListOf(level3A.taskID))
        level1B.setSubTasksConstraintValue(arrayListOf(level2B.taskID))
        level1A.setSubTasksConstraintValue(arrayListOf(level2A.taskID))
        root.setSubTasksConstraintValue(arrayListOf(level1A.taskID, level1B.taskID))

        assertEquals(4, root.getSubTasksLevelsDepth())
    }

    @DisplayName("SubTasks Un-constraining")
    @Test
    fun testTaskSubTasksUnConstraining() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        val task = testTask()
                .setSubTasksConstraintValue(subTasks.taskIDs().toArrayList)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setSubTasksProperty((task.subTasks as Constraint).toProperty())

        sleep(1)

        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("SubTasks Constraint Re-Set")
    @Test
    fun testTaskSubTasksConstraintReSet() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        val task = testTask()
                .setSubTasksConstraintValue(subTasks.taskIDs().toArrayList)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        val newSubTasks = arrayListOf(
                Task("New SubTask1"),
                Task("New SubTask2")
        )

        task.setSubTasksConstraintValue(newSubTasks.taskIDs().toArrayList)
        assertEquals(newSubTasks.taskIDs(), task.subTasks.value)

        newSubTasks.forEach { it.kill() }

        sleep(2)

        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("SubTasks Hiding")
    @Test
    fun testSubTasksHiding() {
        val subTasks = arrayListOf(Task("SubTask1"), Task("SubTask2"))

        val task = testTask()
                .setSubTasksPropertyValue(subTasks.taskIDs().toArrayList)
        assertEquals(subTasks.taskIDs(), task.subTasks.value)

        task.hideSubTasks()
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, task.subTasks)

        task.setSubTasksConstraintValue(subTasks.taskIDs().toArrayList)
        assertEquals(subTasks.taskIDs(), task.subTasks.value)
        assertThrows(IllegalStateException::class.java, { task.hideSubTasks() })
    }

}