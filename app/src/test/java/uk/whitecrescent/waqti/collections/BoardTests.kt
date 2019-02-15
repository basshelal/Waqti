package uk.whitecrescent.waqti.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.size
import uk.whitecrescent.waqti.testBoardFullOfFullLists

class BoardTests : BaseCollectionsTest() {

    @DisplayName("Board RemoveAt")
    @Test
    fun testBoardRemoveAt() {
        val board = testBoardFullOfFullLists
        board.removeAt(0).update()

        assertEquals(9, board.size)
        assertEquals(9, Database.taskLists.size)
        assertEquals(90, Database.tasks.size)
    }
}