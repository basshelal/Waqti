package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.seconds
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_SUB_TASKS
import uk.whitecrescent.waqti.model.task.DEFAULT_SUB_TASKS_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.model.tasks
import uk.whitecrescent.waqti.model.toArrayList
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask

@DisplayName("SubTasks Tests")
class SubTasks : BaseTaskTest() {

    @DisplayName("SubTasks Default Values")
    @Test
    fun testTaskSubTasksDefaultValues() {
        val task = testTask
        assertFalse(task.subTasks.isConstrained)
        assertEquals(DEFAULT_SUB_TASKS, task.subTasks.value)
        assertFalse(task.subTasks.isVisible)
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, Property(HIDDEN, DEFAULT_SUB_TASKS,
                NOT_CONSTRAINED, UNMET))
    }

    @DisplayName("Set SubTasks Property using setSubTasksProperty")
    @Test
    fun testTaskSetSubTasksProperty() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        val subTasksIDs = subTasks.ids

        val task = testTask
                .setSubTasksProperty(
                        Property(SHOWING, subTasksIDs.toArrayList, NOT_CONSTRAINED, UNMET)
                )

        assertFalse(task.subTasks.isConstrained)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)


        task.hideSubTasks()
        assertEquals(Property(HIDDEN, DEFAULT_SUB_TASKS, NOT_CONSTRAINED, UNMET), task.subTasks)
    }

    @DisplayName("Set SubTasks Property using setSubTasksValue")
    @Test
    fun testTaskSetSubTasksValue() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.ids
        val task = testTask
                .setSubTasksPropertyValue(
                        subTasksIDs.toArrayList
                )

        assertFalse(task.subTasks.isConstrained)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)

        task.hideSubTasks()
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, task.subTasks)
    }

    @DisplayName("Set SubTasks Constraint using setSubTasksProperty")
    @Test
    fun testTaskSetSubTasksPropertyWithConstraint() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.ids

        val task = testTask
                .setSubTasksProperty(
                        Property(SHOWING, subTasksIDs.toArrayList, CONSTRAINED, UNMET)
                )

        assertTrue(task.subTasks.isConstrained)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse(task.subTasks.isMet)
    }

    @DisplayName("Set SubTasks Constraint using setSubTasksConstraintValue")
    @Test
    fun testTaskSetSubTasksConstraintValueTask() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.ids

        val task = testTask
                .setSubTasksConstraintValue(subTasksIDs.toArrayList)

        assertTrue(task.subTasks.isConstrained)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse(task.subTasks.isMet)
    }

    @DisplayName("Set SubTasks Property failable")
    @Test
    fun testTaskSetSubTasksPropertyFailable() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.ids

        val task = testTask
                .setSubTasksPropertyValue(subTasksIDs.toArrayList)

        assertFalse(task.isFailable)
        assertFalse(task.subTasks.isConstrained)
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
        val subTasksIDs = subTasks.ids

        val task = testTask
                .setSubTasksProperty(
                        Property(SHOWING, subTasksIDs.toArrayList, CONSTRAINED, UNMET)
                )

        assertTrue(task.isFailable)
        assertTrue(task.subTasks.isConstrained)
        assertEquals(subTasksIDs, task.subTasks.value)
        assertTrue(task.subTasks.isVisible)
        assertFalse(task.subTasks.isMet)
    }

    @DisplayName("Kill with SubTasks Property")
    @Test
    fun testTaskKillWithSubTasksProperty() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )
        val subTasksIDs = subTasks.ids

        val task = testTask
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
        val subTasksIDs = subTasks.ids

        val task = testTask
                .setSubTasksConstraintValue(subTasksIDs.toArrayList)

        assertThrows(TaskStateException::class.java) { task.kill() }

        assertFalse(task.state == TaskState.KILLED)

        Caches.tasks[subTasksIDs[0]].kill()
        Caches.tasks[subTasksIDs[1]].kill()

        assertThrows(TaskStateException::class.java) { task.kill() }

        Caches.tasks[subTasksIDs[2]].kill()

        sleep(2.seconds)

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
        val subTasksIDs = subTasks.ids

        subTasks.forEach { it.isFailable = true }

        val task = testTask
                .setSubTasksConstraintValue(subTasksIDs.toArrayList)

        assertThrows(TaskStateException::class.java) { task.kill() }

        assertFalse(task.state == TaskState.KILLED)

        Caches.tasks[subTasksIDs[0]].fail()
        Caches.tasks[subTasksIDs[1]].kill()
        Caches.tasks[subTasksIDs[2]].kill()

        sleep(2.seconds)

        assertThrows(TaskStateException::class.java) { task.kill() }

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
        val subTasksIDs = subTasks.ids

        val tasks = getTasks(1000)
        tasks.forEach { it.setSubTasksConstraintValue(subTasksIDs.toArrayList) }

        assertThrows(TaskStateException::class.java) { tasks.forEach { it.kill() } }

        Caches.tasks[subTasksIDs[0]].kill()
        Caches.tasks[subTasksIDs[1]].kill()
        Caches.tasks[subTasksIDs[2]].kill()

        sleep(2.seconds)

        tasks.forEach { it.kill() }

    }

    @DisplayName("Before Constraining in SubTasks")
    @Test
    fun testTaskBeforeConstrainingInSubTasks() {
        val subTask2SubTask = Task("SubTask2SubTask")

        val subTask0 = Task("SubTask0")
        val subTask1 = Task("SubTask1")

        val subTask2 = Task("SubTask2")
                .setSubTasksConstraintValue(arrayListOf(subTask2SubTask).ids.toArrayList)
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

                        ).ids.toArrayList
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

    @DisplayName("SubTasks Un-constraining")
    @Test
    fun testTaskSubTasksUnConstraining() {
        val subTasks = arrayListOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        val task = testTask
                .setSubTasksConstraintValue(subTasks.ids.toArrayList)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setSubTasksProperty((task.subTasks).unConstrain())

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

        val task = testTask
                .setSubTasksConstraintValue(subTasks.ids.toArrayList)

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        val newSubTasks = arrayListOf(
                Task("New SubTask1"),
                Task("New SubTask2")
        )

        task.setSubTasksConstraintValue(newSubTasks.ids.toArrayList)
        assertEquals(newSubTasks.ids, task.subTasks.value)

        newSubTasks.forEach { it.kill() }

        sleep(2)

        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("SubTasks Hiding")
    @Test
    fun testSubTasksHiding() {
        val subTasks = arrayListOf(Task("SubTask1"), Task("SubTask2"))

        val task = testTask
                .setSubTasksPropertyValue(subTasks.ids.toArrayList)
        assertEquals(subTasks.ids, task.subTasks.value)

        task.hideSubTasks()
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, task.subTasks)

        task.setSubTasksConstraintValue(subTasks.ids.toArrayList)
        assertEquals(subTasks.ids, task.subTasks.value)
        assertThrows(IllegalStateException::class.java, { task.hideSubTasks() })
    }

    @DisplayName("Get SubTasks List")
    @Test
    fun testTaskGetSubTasksList() {
        val subTasks = listOf(
                Task("SubTask1"),
                Task("SubTask2"),
                Task("SubTask3")
        )

        subTasks.forEach { println(it) }

        val task = testTask

        task.addSubTasks(subTasks)

        sleep(2.seconds)

        val subTasksIDs = subTasks.ids

        subTasksIDs.forEach { println(Caches.tasks.get(it)) }

        assertEquals(subTasksIDs, task.subTasks.value.toList())
        assertEquals(subTasksIDs, task.getSubTasksIDsList())
        assertEquals(subTasksIDs.tasks, task.getSubTasksList())

    }

    /*        @DisplayName("Add SubTasks")
@Test
fun testTaskAddSubTasks() {
    val subTasks = listOf(
            Task("SubTask1"),
            Task("SubTask2"),
            Task("SubTask3")
    )

    assertTrue { subTasks[0] != subTasks[1] && subTasks[1] != subTasks[2] }

    val task = testTask

    task.addSubTasks(subTasks)

    assertFalse(task.subTasks is Constraint)
    assertEquals(subTasks.ids, task.subTasks.value)
    assertTrue(task.subTasks.isVisible)

    task.hideSubTasks()
    assertEquals(DEFAULT_SUB_TASKS_PROPERTY, task.subTasks)
}*/

    /*    @DisplayName("SubTasks depth")
    @Test
    fun testTaskSubTasksDepth() {
        val root = Task("Root")
        val level1 = Task("Level1")
        val level2 = Task("Level2")
        val level3 = Task("Level3")
        val level4 = Task("Level4")

        level3.setSubTasksConstraintValue(arrayListOf(level4.id))
        level2.setSubTasksConstraintValue(arrayListOf(level3.id))
        level1.setSubTasksConstraintValue(arrayListOf(level2.id))
        root.setSubTasksConstraintValue(arrayListOf(level1.id))

        assertThrows(TaskStateException::class.java, { root.kill() })
        assertThrows(TaskStateException::class.java, { level1.kill() })
        assertThrows(TaskStateException::class.java, { level2.kill() })
        assertThrows(TaskStateException::class.java, { level3.kill() })

        // TODO: 23-Jun-18 fails
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

        assertEquals("Level1", rootSubTasks.first().name)
        assertEquals("Level2", level1SubTasks.first().name)
        assertEquals("Level3", level2SubTasks.first().name)
        assertEquals("Level4", level3SubTasks.first().name)
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

    @DisplayName("SubTasks Extra depth")
    @Test
    fun testTaskSubTasksExtraDepth() {

        val list = getTasks(100)

        list.forEachIndexed { index, task ->
            if (index != 99) task.setSubTasksConstraintValue(arrayListOf(list[index + 1].id))
        }

        assertTrue(list[0].subTasks.value.size == 1)

        list.minus(list.last()).reversed().forEach { assertTrue(it.subTasks.value.size == 1) }
        list.minus(list.last()).reversed().forEach {
            assertThrows(TaskStateException::class.java) { it.kill() }
        }

        // TODO: 23-Jun-18 fails
        assertEquals(99, list[0].getSubTasksLevelsDepth())
        assertEquals(50, list[49].getSubTasksLevelsDepth())
        assertEquals(0, list[99].getSubTasksLevelsDepth())

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

        // TODO: 23-Jun-18 fails
        level3A.setSubTasksConstraintValue(arrayListOf(level4A.id))
        level2A.setSubTasksConstraintValue(arrayListOf(level3A.id))
        level1B.setSubTasksConstraintValue(arrayListOf(level2B.id))
        level1A.setSubTasksConstraintValue(arrayListOf(level2A.id))
        root.setSubTasksConstraintValue(arrayListOf(level1A.id, level1B.id))

        assertEquals(4, root.getSubTasksLevelsDepth())
    }*/

}