package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Cache
import uk.whitecrescent.waqti.model.task.ID

@DisplayName("Cache Tests")
class Cache {

    private val cache = TestCaches.cache
    private val stringCache = TestCaches.stringCache

    private fun createCacheables(amount: Int): List<TestCacheable> {
        val list = ArrayList<TestCacheable>(amount)
        for (number in 0 until amount) {
            val cacheable = TestCacheable("AutoCreated Cacheable #$number")
            list.add(cacheable)
        }
        return list.toList()
    }

    @BeforeEach
    fun beforeEach() {
        cache.clear()
        stringCache.clear()
        assertTrue(cache.isEmpty())
        assertTrue(stringCache.isEmpty())
    }

    @AfterEach
    fun afterEach() {
        cache.clear()
        stringCache.clear()
        assertTrue(cache.isEmpty())
        assertTrue(stringCache.isEmpty())
    }

    @DisplayName("Cache New ID")
    @Test
    fun testCacheNewID() {
        (1..1000).forEach {
            val cacheable = TestCacheable("Test $it")
            assertTrue(cacheable in cache)
            assertTrue(cacheable.id in cache.ids)
            assertTrue(cache.count { it.id == cacheable.id } == 1)
        }
        assertTrue(cache.size == 1000)
    }

    @DisplayName("Cache Update")
    @Test
    fun testCacheUpdate() {
        val cacheable = TestCacheable("First")
        assertTrue(cacheable in cache)
        assertTrue(cache[cacheable].name == "First")
        assertTrue(cache.size == 1)

        cacheable.name = "Second"
        assertTrue(cacheable in cache)
        assertTrue(cache[cacheable].name == "Second")
        assertTrue(cache.size == 1)

        cacheable.name = "Third"
        assertTrue(cacheable in cache)
        assertTrue(cache[cacheable].name == "Third")
        assertTrue(cache.size == 1)

    }

    @DisplayName("Cache Size")
    @Test
    fun testCacheSize() {
        createCacheables(10)
        assertEquals(10, cache.size)

        cache.clear()
        assertEquals(0, cache.size)
        assertTrue(cache.isEmpty())
    }

    @DisplayName("Cache Put and Update Element Auto")
    @Test
    fun testCachePutAuto() {
        val new = TestCacheable("New")
        assertEquals(new, cache.query().first())
        assertEquals("New", cache.query().first().name)

        new.name = "Updated"
        assertEquals(new, cache.query().first())
        assertEquals("Updated", cache.query().first().name)

    }

    @DisplayName("Cache Put and Update Element Non Auto")
    @Test
    fun testCachePutNoAuto() {
        val new = CacheableString("New")
        assertTrue(stringCache.isEmpty())

        stringCache.put(new)

        assertEquals(new, stringCache.query().first())
        assertEquals("New", stringCache.query().first().name)

        new.name = "Updated"
        stringCache.put(new)
        assertEquals("Updated", stringCache.query().first().name)

        assertEquals(1, stringCache.size)

    }

    @DisplayName("Cache Put and Update Collection Auto")
    @Test
    fun testCachePutCollectionAuto() {
        cache.put(createCacheables(10))
        assertEquals(10, cache.size)

        cache[cache.query()[4].id].name = "Updated!"
        assertEquals(cache.query()[4].name, "Updated!")

    }

    @DisplayName("Cache Put and Update Collection No Auto")
    @Test
    fun testCachePutCollectionNoAuto() {
        val list = listOf(
                CacheableString("First"),
                CacheableString("Second"),
                CacheableString("Third")
        )
        assertTrue(stringCache.isEmpty())

        stringCache.put(list)
        assertEquals(3, stringCache.size)

        stringCache[stringCache.query()[1].id].name = "Updated!"
        assertEquals(stringCache.query()[1].name, "Updated!")
    }

    @DisplayName("Cache Different Safe Get")
    @Test
    fun testCacheDifferentSafeGet() {
        val cache0 = object : Cache<CacheableString>() {
            override fun safeGet(id: ID): CacheableString {
                throw NoSuchElementException("Testing!")
            }
        }
        val string = CacheableString("String")
        cache0.put(string)
        assertThrows(NoSuchElementException::class.java) { cache0[string] }
    }

}