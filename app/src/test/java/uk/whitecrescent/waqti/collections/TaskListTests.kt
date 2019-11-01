package uk.whitecrescent.waqti.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.extensions.size
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.testTask
import uk.whitecrescent.waqti.testTaskListEmpty
import uk.whitecrescent.waqti.testTaskListFull

class TaskListTests : BaseCollectionsTest() {

    @DisplayName("TaskList Initialized")
    @Test
    fun testTaskListInitialized() {
        val list = testTaskListEmpty
        assertTrue(list.isEmpty())
        assertTrue(list in Database.taskLists.all)
    }

    @DisplayName("TaskList Add element")
    @Test
    fun testTaskListAddElement() {
        val list = testTaskListEmpty
        list.add(testTask)
        assertEquals(1, list.size)

        assertFalse(Database.taskLists.isEmpty)
        assertFalse(Database.tasks.isEmpty)
        assertTrue(Database.taskLists[list.id].isEmpty())

        list.update()

        assertEquals(1, Database.taskLists[list.id].size)
    }

    @DisplayName("TaskList Add elements")
    @Test
    fun testTaskListAddElements() {
        val list = testTaskListEmpty
        list.addAll(getTasks(10))
        assertEquals(10, list.size)

        assertFalse(Database.taskLists.isEmpty)
        assertFalse(Database.tasks.isEmpty)

        assertEquals(1, Database.taskLists.size)
        assertEquals(10, Database.tasks.size)

        assertTrue(Database.taskLists[list.id].isEmpty())

        list.update()

        assertEquals(10, Database.taskLists[list.id].size)

        // TODO: 25-Dec-18 Abrupt Cache closing makes our Tasks not even know when to stop observing
        // I think, this is only really a problem in Testing and does not affect any production code
        // a solution would be to have every Cacheable include a finalize() method as well, bit
        // overkill though
    }

    @DisplayName("TaskList RemoveAt")
    @Test
    fun testTaskListRemoveAt() {
        val list = testTaskListFull
        list.removeAt(0).update()


        assertEquals(9, list.size)
        assertEquals(1, Database.taskLists.size)
        assertEquals(9, Database.tasks.size)

    }
}