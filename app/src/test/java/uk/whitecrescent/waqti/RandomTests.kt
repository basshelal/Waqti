package uk.whitecrescent.waqti

import io.objectbox.kotlin.boxFor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.TestEntity
import uk.whitecrescent.waqti.model.persistence.Cache
import uk.whitecrescent.waqti.model.persistence.Database
import java.io.File

@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Random 1")
    @Test
    fun testRandom1() {
        Database.buildTest(File("DEBUG_DB"))
        val db = Database.store.boxFor<TestEntity>()
        val cache = Cache<TestEntity>(db)

        val entity = TestEntity(name = "Name", number = 1)

        assertEquals(0, entity.id)

        cache.put(entity)

        assertEquals(1, entity.id)

        cache.put(entity)

        assertEquals(1, db.count())
        assertEquals(1, cache.count())

        assertEquals(2, cache.newID)

        assertEquals(entity, db[1])

        db.store.close()
        db.store.deleteAllFiles()
        Database.store.close()
        Database.store.deleteAllFiles()
    }
}