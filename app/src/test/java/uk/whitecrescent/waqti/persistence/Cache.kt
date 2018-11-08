package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.TestEntity
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.sleep

@DisplayName("Cache Tests")
class Cache : BasePersistenceTest() {

    private fun createEntities(amount: Int): List<TestEntity> {
        val list = ArrayList<TestEntity>(amount)
        for (number in 0 until amount) {
            val cacheable = TestEntity(name = "AutoCreated Cacheable #$number", number = number)
            list.add(cacheable)
        }
        return list
    }

    @DisplayName("Cache New ID")
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
        assertTrue(cacheable in Database.testEntityDB.all)

        assertTrue(cache[cacheable].name == "First")
        assertTrue(Database.testEntityDB[cacheable.id].name == "First")

        assertTrue(cache.size == 1)
        assertTrue(Database.testEntityDB.count().toInt() == 1)

        cacheable.name = "Second"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.testEntityDB.all)

        assertTrue(cache[cacheable].name == "Second")
        assertTrue(Database.testEntityDB[cacheable.id].name == "Second")

        assertTrue(cache.size == 1)
        assertTrue(Database.testEntityDB.count().toInt() == 1)

        cacheable.name = "Third"

        assertTrue(cacheable in cache)
        assertTrue(cacheable in Database.testEntityDB.all)

        assertTrue(cache[cacheable].name == "Third")
        assertTrue(Database.testEntityDB[cacheable.id].name == "Third")

        assertTrue(cache.size == 1)
        assertTrue(Database.testEntityDB.count().toInt() == 1)

    }

    @DisplayName("Cache Size")
    @Test
    fun testCacheSize() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntityDB.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.testEntityDB.count())
        assertTrue(Database.testEntityDB.all.isNotEmpty())
    }

    @DisplayName("Cache Put and Update Element Auto")
    @Test
    fun testCachePutAuto() {
        val cache = Caches.testEntities
        val new = TestEntity(name = "New")
        assertEquals(new, cache.query().first())
        assertEquals(new, Database.testEntityDB[new.id])

        assertEquals("New", cache.query().first().name)
        assertEquals("New", Database.testEntityDB[new.id].name)

        new.name = "Updated"

        assertEquals(new, cache.query().first())
        assertEquals(new, Database.testEntityDB[new.id])

        assertEquals("Updated", cache.query().first().name)
        assertEquals("Updated", Database.testEntityDB[new.id].name)
    }

    @DisplayName("Cache Put and Update Collection Auto")
    @Test
    fun testCachePutCollectionAuto() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntityDB.count())

        val randomElement = cache.query()[69]

        cache[randomElement.id].name = "Updated!"
        assertEquals(randomElement.name, "Updated!")
        assertTrue(Database.testEntityDB[randomElement.id].name == "Updated!")

    }

    @DisplayName("Cache Update")
    @Test
    fun testCacheUpdate() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntityDB.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.testEntityDB.count())
        assertTrue(Database.testEntityDB.all.isNotEmpty())

        cache.update()

        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntityDB.count())

        assertEquals(Database.testEntityDB.all.sortedBy { it.id }, cache.query().sortedBy { it.id })
    }

    @DisplayName("Async Check")
    @Test
    fun testAsyncCheck() {
        val cache = Caches.testEntities
        createEntities(100)
        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntityDB.count())

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())

        assertEquals(100, Database.testEntityDB.count())
        assertTrue(Database.testEntityDB.all.isNotEmpty())

        cache.checkSeconds = 1L
        cache.asyncCheck()

        sleep(2)

        assertEquals(100, cache.size)
        assertEquals(100, Database.testEntityDB.count())

        assertEquals(Database.testEntityDB.all.sortedBy { it.id }, cache.query().sortedBy { it.id })
    }

}