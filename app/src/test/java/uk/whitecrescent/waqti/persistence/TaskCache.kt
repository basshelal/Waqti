package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.extensions.ids
import uk.whitecrescent.waqti.extensions.size
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.sleep

@DisplayName("Task Cache Tests")
class TaskCache : BasePersistenceTest() {

    @DisplayName("Task Cache Update")
    @Test
    fun testTaskCacheUpdate() {
        getTasks(100)
        assertEquals(100, Caches.tasks.size)
        assertEquals(100, Database.tasks.size)

        Caches.tasks.clearAll().commit()
        assertEquals(0, Caches.tasks.size)
        assertEquals(0, Database.tasks.size)
    }

    @DisplayName("Cache Unique IDs")
    @Test
    fun testCacheNewID() {
        getTasks(100).forEach {
            assertTrue(it in Caches.tasks)
            assertTrue(it.id in Caches.tasks.ids)
            assertTrue(Caches.tasks.distinct().size == 100)
        }
        assertTrue(Caches.tasks.size == 100)
    }

    @DisplayName("Cache Update Element Properties")
    @Test
    fun testCacheUpdateElementProperties() {
        val cache = Caches.tasks
        val cacheable = Task(name = "First")

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.tasks.all)

        assertTrue(cache[cacheable].name == "First")
        assertTrue(Database.tasks[cacheable.id].name == "First")

        assertTrue(cache.size == 1)
        assertTrue(Database.tasks.count().toInt() == 1)

        cacheable.changeName("Second")

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.tasks.all)

        assertTrue(cache[cacheable].name == "Second")
        assertTrue(Database.tasks[cacheable.id].name == "Second")

        assertTrue(cache.size == 1)
        assertTrue(Database.tasks.count().toInt() == 1)

        cacheable.changeName("Third")

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.tasks.all)

        assertTrue(cache[cacheable].name == "Third")
        assertTrue(Database.tasks[cacheable.id].name == "Third")

        assertTrue(cache.size == 1)
        assertTrue(Database.tasks.count().toInt() == 1)

    }

    @DisplayName("Cache Size")
    @Test
    fun testCacheSize() {
        val cache = Caches.tasks
        getTasks(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())
    }

    @DisplayName("Cache Put and Update Element Auto")
    @Test
    fun testCachePutAuto() {
        val cache = Caches.tasks
        val new = Task(name = "New")
        assertEquals(new, cache.valueList().first())
        assertEquals(new, Database.tasks[new.id])

        assertEquals("New", cache.valueList().first().name)
        assertEquals("New", Database.tasks[new.id].name)

        new.changeName("Updated")

        assertEquals(new, cache.valueList().first())
        assertEquals(new, Database.tasks[new.id])

        assertEquals("Updated", cache.valueList().first().name)
        assertEquals("Updated", Database.tasks[new.id].name)
    }

    @DisplayName("Cache Put and Update Collection Auto")
    @Test
    fun testCachePutCollectionAuto() {
        val cache = Caches.tasks
        getTasks(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())

        val randomElement = cache.valueList()[69]

        cache[randomElement.id].changeName("Updated!")
        assertEquals(randomElement.name, "Updated!")
        assertTrue(Database.tasks[randomElement.id].name == "Updated!")

    }

    @DisplayName("Cache Update")
    @Test
    fun testCacheUpdate() {
        val cache = Caches.tasks
        getTasks(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.size)

        cache.clearAll().commit()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())
    }

    @DisplayName("Cache Trim")
    @Test
    fun testCacheTrim() {
        val cache = Caches.tasks
        getTasks(1500)

        assertEquals(1500, cache.size)
        assertEquals(1500, Database.tasks.size)

        cache.trim()
        assertEquals(1000, cache.size)
        assertEquals(1500, Database.tasks.size)

        sleep(2)
    }
}