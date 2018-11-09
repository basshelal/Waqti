package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.task.Task

@DisplayName("Task Cache & Database Tests")
class TaskCache : BasePersistenceTest() {

    private fun createEntities(amount: Int) =
            Array(amount, { Task(title = "AutoCreated Task #$it") }).toList()

    @DisplayName("Test")
    @Test
    fun test() {
        createEntities(100)
        assertEquals(100, Caches.tasks.size)
        assertEquals(100, Database.tasks.size)
    }

    @DisplayName("Task Cache Update")
    @Test
    fun testTaskCacheUpdate() {
        createEntities(100)
        assertEquals(100, Caches.tasks.size)
        assertEquals(100, Database.tasks.size)

        Caches.tasks.clear()
        assertEquals(0, Caches.tasks.size)
        assertEquals(100, Database.tasks.size)

        Caches.tasks.update()
        assertEquals(100, Caches.tasks.size)
        assertEquals(100, Database.tasks.count())
    }
}