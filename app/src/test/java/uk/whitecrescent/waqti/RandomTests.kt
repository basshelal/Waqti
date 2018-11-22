package uk.whitecrescent.waqti

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.persistence.BasePersistenceTest

@DisplayName("Random Tests")
class RandomTests : BasePersistenceTest() {

    @DisplayName("Task List")
    @Test
    fun testTaskList() {
        val board = Board("MY BOARD")
        board.add(TaskList("MY TASK LIST0", getTasks(100)))
        board.add(TaskList("MY TASK LIST1", getTasks(100)))
        board.add(TaskList("MY TASK LIST2", getTasks(100)))
        board.update()

        assertTrue(Database.boards.size == 1)
        assertTrue(Database.boards[1].size == 3)
        assertTrue(Database.boards[1][1].size == 100)
        assertTrue(Database.boards[1][2].size == 100)
        assertTrue(Database.boards[1][1] == board[0])
    }

    @DisplayName("Test")
    @Test
    fun test() {
        val x = Template("TEST", testTask)
        assertTrue(Caches.templates.isNotEmpty())
        assertEquals(testTask.getAllProperties(),
                Task.fromTemplate(Caches.templates[1], "Task").getAllProperties())
    }

}