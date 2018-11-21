package uk.whitecrescent.waqti.persistence

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.Task

@DisplayName("Task Cache Tests")
class TaskCache : BasePersistenceTest() {

    @DisplayName("Task Cache Update")
    @Test
    fun testTaskCacheUpdate() {
        getTasks(100)
        assertEquals(100, Caches.tasks.size)
        assertEquals(100, Database.tasks.size)

        Caches.tasks.clear()
        assertEquals(0, Caches.tasks.size)
        assertEquals(100, Database.tasks.size)

        Caches.tasks.update()
        assertEquals(100, Caches.tasks.size)
        assertEquals(100, Database.tasks.count())
    }

    @DisplayName("Cache Unique IDs")
    @Test
    fun testCacheNewID() {
        (1..100).forEach {
            val cacheable = Task(name = "Test $it")
            assertTrue(cacheable in Caches.tasks)
            assertTrue(cacheable.id in Caches.tasks.ids)
            assertTrue(Caches.tasks.count { it.id == cacheable.id } == 1)
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

        cacheable.name = "Second"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.tasks.all)

        assertTrue(cache[cacheable].name == "Second")
        assertTrue(Database.tasks[cacheable.id].name == "Second")

        assertTrue(cache.size == 1)
        assertTrue(Database.tasks.count().toInt() == 1)

        cacheable.name = "Third"

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

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.tasks.count())
        assertTrue(Database.tasks.all.isNotEmpty())
    }

    @DisplayName("Cache Put and Update Element Auto")
    @Test
    fun testCachePutAuto() {
        val cache = Caches.tasks
        val new = Task(name = "New")
        assertEquals(new, cache.query().first())
        assertEquals(new, Database.tasks[new.id])

        assertEquals("New", cache.query().first().name)
        assertEquals("New", Database.tasks[new.id].name)

        new.name = "Updated"

        assertEquals(new, cache.query().first())
        assertEquals(new, Database.tasks[new.id])

        assertEquals("Updated", cache.query().first().name)
        assertEquals("Updated", Database.tasks[new.id].name)
    }

    @DisplayName("Cache Put and Update Collection Auto")
    @Test
    fun testCachePutCollectionAuto() {
        val cache = Caches.tasks
        getTasks(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())

        val randomElement = cache.query()[69]

        cache[randomElement.id].name = "Updated!"
        assertEquals(randomElement.name, "Updated!")
        assertTrue(Database.tasks[randomElement.id].name == "Updated!")

    }

    @DisplayName("Cache Update")
    @Test
    fun testCacheUpdate() {
        val cache = Caches.tasks
        getTasks(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.tasks.count())
        assertTrue(Database.tasks.all.isNotEmpty())

        cache.update()

        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())

        assertEquals(Database.tasks.all.sortedBy { it.id }, cache.query().sortedBy { it.id })
    }

    @Disabled
    @DisplayName("Async Check")
    @Test
    fun testAsyncCheck() {
        val cache = Caches.tasks
        getTasks(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.tasks.count())
        assertTrue(Database.tasks.all.isNotEmpty())

        cache.startAsyncCheck(1L)

        sleep(2)

        assertEquals(100, cache.size)
        assertEquals(100, Database.tasks.count())

        assertEquals(Database.tasks.all.sortedBy { it.id }, cache.query().sortedBy { it.id })
        cache.stopAsyncCheck()
    }

    @Disabled
    @DisplayName("Concurrent")
    @Test
    fun testConcurrent() {
        val cache = Caches.tasks
        Observable.fromCallable { getTasks(1000) }.subscribeOn(Schedulers.newThread())
                .subscribe()
        Observable.fromCallable { getTasks(1000) }.subscribeOn(Schedulers.newThread())
                .subscribe()

        sleep(4) // how do we have blocking reading? Concurrent Read is important!

        assertEquals(2000, cache.size)
        assertEquals(2000, Database.tasks.count())
    }
}