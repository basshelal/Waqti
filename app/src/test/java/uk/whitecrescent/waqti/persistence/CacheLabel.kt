package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.task.Label

@DisplayName("Label Cache & Database Tests")
class CacheLabel : BasePersistenceTest() {

    private fun createEntities(amount: Int) = Array(amount, { Label(name = "AutoCreated Label #$it") }).toList()

    @DisplayName("Label Cache Create")
    @Test
    fun testLabelCacheCreate() {
        createEntities(100)
        assertEquals(100, Caches.labels.size)
        assertEquals(100, Database.labels.size)

        Caches.labels.clear()
        assertEquals(0, Caches.labels.size)
        assertEquals(100, Database.labels.size)
    }

    @DisplayName("Label Cache Update")
    @Test
    fun testLabelCacheUpdate() {
        createEntities(100)
        assertEquals(100, Caches.labels.size)
        assertEquals(100, Database.labels.size)

        Caches.labels.clear()
        assertEquals(0, Caches.labels.size)
        assertEquals(100, Database.labels.size)

        Caches.labels.update()
        assertEquals(100, Caches.labels.size)
        assertEquals(100, Database.labels.count())
    }

}