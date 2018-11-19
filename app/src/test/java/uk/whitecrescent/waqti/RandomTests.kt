package uk.whitecrescent.waqti

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.collections.TaskList

@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Task List")
    @Test
    fun testTaskList() {
        val list = TaskList()
        println(list.tasks.size)
    }

}