package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.DB_BUILT
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import java.io.File

open class BasePersistenceTest {

    companion object {

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            if (!DB_BUILT || Database.store.isClosed) {
                Database.buildTest(File("DEBUG_DB"))
                DB_BUILT = true
            }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {

        }

    }

    @BeforeEach
    fun beforeEach() {
        Database.clearAllDBs().commit()
        Caches.clearAllCaches().commit()

        Database.allDBs.forEach { assertTrue(it.size == 0) }
        Caches.allCaches.forEach { assertTrue(it.isEmpty()) }
    }

    @AfterEach
    fun afterEach() {
        Database.clearAllDBs().commit()
        Caches.clearAllCaches().commit()
        Database.allDBs.forEach { assertTrue(it.count().toInt() == 0) }
        Caches.allCaches.forEach { assertTrue(it.isEmpty()) }
    }
}