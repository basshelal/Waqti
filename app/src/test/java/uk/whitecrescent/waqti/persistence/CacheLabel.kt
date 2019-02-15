package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.DisplayName
import uk.whitecrescent.waqti.backend.task.Label

@DisplayName("Label Cache & Database Tests")
class CacheLabel : BasePersistenceTest() {

    private fun createEntities(amount: Int) = Array(amount, { Label(name = "AutoCreated Label #$it") }).toList()

//    @DisplayName("Label Cache Create")
//    @Test
//    fun testLabelCacheCreate() {
//        createEntities(100)
//        assertEquals(100, Caches.labels.size)
//        assertEquals(100, Database.labels.size)
//
//        Caches.labels.clearMap()
//        assertEquals(0, Caches.labels.size)
//        assertEquals(100, Database.labels.size)
//    }
//
//    @DisplayName("Label Cache Update")
//    @Test
//    fun testLabelCacheUpdate() {
//        createEntities(100)
//        assertEquals(100, Caches.labels.size)
//        assertEquals(100, Database.labels.size)
//
//        Caches.labels.clearMap()
//        assertEquals(0, Caches.labels.size)
//        assertEquals(100, Database.labels.size)
//
//        Caches.labels.update()
//        assertEquals(100, Caches.labels.size)
//        assertEquals(100, Database.labels.count())
//    }

}