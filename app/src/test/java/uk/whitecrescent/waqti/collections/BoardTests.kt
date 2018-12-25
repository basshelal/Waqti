package uk.whitecrescent.waqti.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.size

class BoardTests : BaseCollectionsTest() {

    val testBoard: Board
        get() = Board("Board")

    @DisplayName("Test")
    @Test
    fun test2() {
        val board = testBoard
        board.addAll(Array(10, { TaskList("$it") }).toList()).update()
        assertTrue(board.size == 10)
        assertEquals(10, Database.taskLists[board.id].size)
        assertEquals(10, Database.tasks.size)

        board.clear().update()
        assertTrue(board.size == 0)
        assertEquals(0, Database.taskLists[board.id].size)
        assertEquals(0, Database.tasks.size)
    }
}