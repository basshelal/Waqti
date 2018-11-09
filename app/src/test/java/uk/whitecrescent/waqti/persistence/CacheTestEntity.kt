package uk.whitecrescent.waqti.persistence

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.TestEntity
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.sleep

@DisplayName("Test Entity Cache & Database Tests")
class CacheTestEntity : BasePersistenceTest() {

    private fun createEntities(amount: Int): List<TestEntity> {
        val list = ArrayList<TestEntity>(amount)
        for (number in 0 until amount) {
            val cacheable = TestEntity(name = "AutoCreated Cacheable #$number", number = number)
            list.add(cacheable)
        }
        return list
    }

    @DisplayName("Cache Unique IDs")
    @Test
    fun testCacheNewID() {
        (1..100).forEach {
            val cacheable = TestEntity(name = "Test $it")
            assertTrue(cacheable in Caches.testEntities)
            assertTrue(cacheable.id in Caches.testEntities.ids)
            assertTrue(Caches.testEntities.count { it.id == cacheable.id } == 1)
        }
        assertTrue(Caches.testEntities.size == 100)
    }

    @DisplayName("Cache Update Element Properties")
    @Test
    fun testCacheUpdateElementProperties() {
        val cache = Caches.testEntities
        val cacheable = TestEntity(name = "First")

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.testEntities.all)

        assertTrue(cache[cacheable].name == "First")
        assertTrue(Database.testEntities[cacheable.id].name == "First")

        assertTrue(cache.size == 1)
        assertTrue(Database.testEntities.count().toInt() == 1)

        cacheable.name = "Second"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.testEntities.all)

        assertTrue(cache[cacheable].name == "Second")
        assertTrue(Database.testEntities[cacheable.id].name == "Second")

        assertTrue(cache.size == 1)
        assertTrue(Database.testEntities.count().toInt() == 1)

        cacheable.name = "Third"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.testEntities.all)

        assertTrue(cache[cacheable].name == "Third")
        assertTrue(Database.testEntities[cacheable.id].name == "Third")

        assertTrue(cache.size == 1)
        assertTrue(Database.testEntities.count().toInt() == 1)

    }

    @DisplayName("Cache Size")
    @Test
    fun testCacheSize() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntities.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.testEntities.count())
        assertTrue(Database.testEntities.all.isNotEmpty())
    }

    @DisplayName("Cache Put and Update Element Auto")
    @Test
    fun testCachePutAuto() {
        val cache = Caches.testEntities
        val new = TestEntity(name = "New")
        assertEquals(new, cache.query().first())
        assertEquals(new, Database.testEntities[new.id])

        assertEquals("New", cache.query().first().name)
        assertEquals("New", Database.testEntities[new.id].name)

        new.name = "Updated"

        assertEquals(new, cache.query().first())
        assertEquals(new, Database.testEntities[new.id])

        assertEquals("Updated", cache.query().first().name)
        assertEquals("Updated", Database.testEntities[new.id].name)
    }

    @DisplayName("Cache Put and Update Collection Auto")
    @Test
    fun testCachePutCollectionAuto() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntities.count())

        val randomElement = cache.query()[69]

        cache[randomElement.id].name = "Updated!"
        assertEquals(randomElement.name, "Updated!")
        assertTrue(Database.testEntities[randomElement.id].name == "Updated!")

    }

    @DisplayName("Cache Update")
    @Test
    fun testCacheUpdate() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntities.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.testEntities.count())
        assertTrue(Database.testEntities.all.isNotEmpty())

        cache.update()

        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntities.count())

        assertEquals(Database.testEntities.all.sortedBy { it.id }, cache.query().sortedBy { it.id })
    }

    @DisplayName("Async Check")
    @Test
    fun testAsyncCheck() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntities.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.testEntities.count())
        assertTrue(Database.testEntities.all.isNotEmpty())

        cache.startAsyncCheck(1L)

        sleep(2)

        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntities.count())

        assertEquals(Database.testEntities.all.sortedBy { it.id }, cache.query().sortedBy { it.id })
        cache.stopAsyncCheck()
    }

    @DisplayName("Concurrent")
    @Test
    fun testConcurrent() {
        val cache = Caches.testEntities
        Observable.fromCallable { createEntities(1000) }.subscribeOn(Schedulers.newThread())
                .subscribe()
        Observable.fromCallable { createEntities(1000) }.subscribeOn(Schedulers.newThread())
                .subscribe()

        sleep(4) // how do we have blocking reading? Concurrent Read is important!

        assertEquals(2000, cache.size)
        assertEquals(2000, Database.testEntities.count())
    }

}