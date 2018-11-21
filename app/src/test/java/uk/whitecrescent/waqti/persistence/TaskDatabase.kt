package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.forEach
import uk.whitecrescent.waqti.model.isEmpty
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.testTask

@DisplayName("Task Database Tests")
class TaskDatabase : BasePersistenceTest() {

    @DisplayName("Task Put")
    @Test
    fun testTaskPut() {
        val task = testTask
        Database.tasks.put(task)

        assertEquals(task, Database.tasks[task.id])
        assertTrue(Database.tasks.size == 1)
    }

    @DisplayName("Task Put Many")
    @Test
    fun testTaskPutMany() {
        val tasks = getTasks(100)
        Database.tasks.put(tasks)

        assertEquals(tasks, Database.tasks.all)
        assertTrue(Database.tasks.size == tasks.size)
    }

    @DisplayName("Task Update")
    @Test
    fun testTaskUpdate() {
        val task = Task("Initial Name")
        Database.tasks.put(task)

        assertEquals("Initial Name", Database.tasks[task.id].name)

        task.name = "New Name"
        Database.tasks.put(task)

        assertEquals("New Name", Database.tasks[task.id].name)

        assertEquals(task, Database.tasks[task.id])
        assertTrue(Database.tasks.size == 1)
    }

    @DisplayName("Task Update Many")
    @Test
    fun testTaskUpdateMany() {
        val tasks = getTasks(100)
        Database.tasks.put(tasks)

        tasks.forEach { it.name = "New Name" }
        Database.tasks.put(tasks)

        Database.tasks.forEach { assertEquals("New Name", it.name) }

        assertEquals(tasks, Database.tasks.all)
        assertTrue(Database.tasks.size == tasks.size)
    }

    @DisplayName("Task Delete")
    @Test
    fun testTaskDelete() {
        val task = testTask
        Database.tasks.put(task)

        assertEquals(task, Database.tasks[task.id])
        assertTrue(Database.tasks.size == 1)

        Database.tasks.remove(task)

        assertNull(Database.tasks[task.id])
        assertTrue(Database.tasks.isEmpty())
    }

    @DisplayName("Task Delete Many")
    @Test
    fun testTaskDeleteMany() {
        val tasks = getTasks(100)
        Database.tasks.put(tasks)

        assertEquals(tasks, Database.tasks.all)
        assertTrue(Database.tasks.size == tasks.size)

        Database.tasks.remove(tasks)

        tasks.forEach { assertNull(Database.tasks[it.id]) }
        assertTrue(Database.tasks.isEmpty())
    }
}