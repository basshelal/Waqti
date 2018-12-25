package uk.whitecrescent.waqti.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.size
import uk.whitecrescent.waqti.model.task.Task

class TaskListTests : BaseCollectionsTest() {

    val testList: TaskList
        get() = TaskList("TaskList")

    @DisplayName("Test")
    @Test
    fun test() {
        val list = testList
        list.add(Task("Hello"))
        assertTrue(list.size == 1)
    }

    @DisplayName("Test")
    @Test
    fun test1() {
        val list = testList
        list.addAll(getTasks(10)).update()
        assertTrue(list.size == 10)
        assertEquals(10, Database.taskLists[list.id].size)
        assertEquals(10, Database.tasks.size)
    }

    @DisplayName("Test")
    @Test
    fun test2() {
        val list = testList
        list.addAll(getTasks(10)).update()
        assertTrue(list.size == 10)
        assertEquals(10, Database.taskLists[list.id].size)
        assertEquals(10, Database.tasks.size)

        list.clear().update()
        assertTrue(list.size == 0)
        assertEquals(0, Database.taskLists[list.id].size)
        assertEquals(0, Database.tasks.size)
    }
}