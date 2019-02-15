package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.backend.task.Label
import uk.whitecrescent.waqti.getLabels
import uk.whitecrescent.waqti.ids
import uk.whitecrescent.waqti.size
import uk.whitecrescent.waqti.sleep
import java.util.concurrent.TimeUnit

@DisplayName("Cache Tests")
class CacheTests : BasePersistenceTest() {

    val amount = 100

    @DisplayName("Cache Unique IDs")
    @Test
    fun testCacheNewID() {
        getLabels(amount).forEach {
            assertTrue(it in Caches.labels)
            assertTrue(it.id in Caches.labels.ids)
            assertTrue(Caches.labels.distinct().size == amount)
            assertTrue(Caches.labels.count { i -> it.id == i.id } == 1)
        }
        assertTrue(Caches.labels.size == amount)
    }

    @DisplayName("Cache Auto Put")
    @Test
    fun testCacheAutoPut() {
        getLabels(amount)
        assertEquals(amount, Caches.labels.size)
        assertEquals(amount, Database.labels.size)
    }

    @DisplayName("Cache Size")
    @Test
    fun testCacheSize() {
        getLabels(amount)
        assertEquals(amount, Caches.labels.size)
        assertEquals(amount, Database.labels.size)
    }

    @DisplayName("Cache Clear All")
    @Test
    fun testTaskCacheUpdate() {
        getLabels(amount)
        assertEquals(amount, Caches.labels.size)
        assertEquals(amount, Database.labels.size)

        Caches.labels.clearAll().commit()
        assertEquals(0, Caches.tasks.size)
        assertEquals(0, Database.tasks.size)
    }

    @DisplayName("Cache Update Element Properties")
    @Test
    fun testCacheUpdateElementProperties() {
        val cacheable = Label("First")
        val cache = Caches.labels

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.labels.all)

        assertTrue(cache[cacheable].name == "First")
        assertTrue(Database.labels[cacheable.id].name == "First")

        assertTrue(cache.size == 1)
        assertTrue(Database.labels.size == 1)

        cacheable.name = "Second"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.labels.all)

        assertTrue(cache[cacheable].name == "Second")
        assertTrue(Database.labels[cacheable.id].name == "Second")

        assertTrue(cache.size == 1)
        assertTrue(Database.labels.size == 1)

        cacheable.name = "Third"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.labels.all)

        assertTrue(cache[cacheable].name == "Third")
        assertTrue(Database.labels[cacheable.id].name == "Third")

        assertTrue(cache.size == 1)
        assertTrue(Database.labels.size == 1)

    }

    @DisplayName("Cache Put and Update Collection Auto")
    @Test
    fun testCachePutCollectionAuto() {
        val cache = Caches.labels
        getLabels(amount)
        assertEquals(amount, cache.size)
        assertEquals(amount, Database.labels.size)

        val randomElement = cache.valueList()[69]

        cache[randomElement.id].name = "Updated!"

        assertEquals(randomElement.name, "Updated!")
        assertTrue(Database.labels[randomElement.id].name == "Updated!")

    }

    @DisplayName("Cache Clear All")
    @Test
    fun testCacheClearAll() {
        val cache = Caches.labels
        getLabels(amount)
        assertEquals(amount, cache.size)
        assertEquals(amount, Database.labels.size)

        cache.clearAll().commit()
        assertEquals(0, cache.size)
        assertEquals(0, Database.labels.size)

    }

    @DisplayName("Cache Trim")
    @Test
    fun testCacheTrim() {
        val amount = 150
        val cache = Caches.labels
        cache.sizeLimit = 100

        getLabels(amount)

        assertEquals(amount, cache.size)
        assertEquals(amount, Database.labels.size)

        cache.trim()
        assertEquals(cache.sizeLimit, cache.size)
        assertEquals(amount, Database.labels.size)

    }

    @DisplayName("Cache Async Trim")
    @Test
    fun testCacheAsyncTrim() {
        val cache = Caches.labels
        cache.sizeLimit = 50

        val list = getLabels(amount)

        assertEquals(amount, cache.size)

        cache.startAsyncCheck(100L, TimeUnit.MILLISECONDS)

        sleep(1)

        assertEquals(50, cache.size)
        assertEquals(list.subList(50, amount), cache.valueList())

        cache.close()
    }

    @DisplayName("Cache Async Clean")
    @Test
    fun testCacheAsyncClean() {
        val cache = Caches.labels

        val list = getLabels(amount)

        assertEquals(list, cache.valueList())

        cache.startAsyncCheck(1L, TimeUnit.SECONDS)

        assertEquals(list, cache.valueList())

        Database.labels.remove(69)
        sleep(2)

        assertEquals(amount - 1, cache.size)
        assertTrue(69 !in cache.idList())

        cache.close()
    }

    @DisplayName("Cache Close")
    @Test
    fun testCacheClose() {
        val cache = Caches.labels
        cache.startAsyncCheck(100L, TimeUnit.MILLISECONDS)

        getLabels(amount)
        assertEquals(amount, Caches.labels.size)
        assertEquals(amount, Database.labels.size)

        sleep(1)

        cache.close()
        assertTrue(cache.isEmpty())
        assertEquals(amount, Database.labels.size)
    }
}