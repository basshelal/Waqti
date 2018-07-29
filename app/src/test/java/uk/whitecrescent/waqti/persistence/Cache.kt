package uk.whitecrescent.waqti.persistence

import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.ids

@DisplayName("Cache Tests")
class Cache {

    @BeforeEach
    fun beforeEach() {
        TestCaches.testCache.clear()
        assertTrue(TestCaches.testCache.isEmpty())
    }

    @AfterEach
    fun afterEach() {
        TestCaches.testCache.clear()
        assertTrue(TestCaches.testCache.isEmpty())
    }

    @DisplayName("Test Cache New ID")
    @Test
    fun testCacheNewID() {
        (1..1000).forEach {
            val cacheable = TestCacheable("Test $it")
            assertTrue(cacheable in TestCaches.testCache)
            assertTrue(cacheable.id in TestCaches.testCache.ids)
            assertTrue(TestCaches.testCache.count { it.id == cacheable.id } == 1)
        }
        assertTrue(TestCaches.testCache.size == 1000)
    }

    @DisplayName("Test Cache Update")
    @Test
    fun testCacheUpdate() {
        val cacheable = TestCacheable("First")
        assertTrue(cacheable in TestCaches.testCache)
        assertTrue(TestCaches.testCache[cacheable].name == "First")
        assertTrue(TestCaches.testCache.size == 1)

        cacheable.name = "Second"
        assertTrue(cacheable in TestCaches.testCache)
        assertTrue(TestCaches.testCache[cacheable].name == "Second")
        assertTrue(TestCaches.testCache.size == 1)

        cacheable.name = "Third"
        assertTrue(cacheable in TestCaches.testCache)
        assertTrue(TestCaches.testCache[cacheable].name == "Third")
        assertTrue(TestCaches.testCache.size == 1)

    }

}