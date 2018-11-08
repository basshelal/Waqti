package uk.whitecrescent.waqti.persistence

import io.objectbox.exception.DbException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import java.io.File

open class BasePersistenceTest {

    private val dir = File("DEBUG_DB")

    @BeforeEach
    fun beforeEach() {
        // This is a hack until I figure out how to fix this damn thing!!
        // Can't build when it already exists
        try {
            println("BUILDING...")
            Database.buildTest(dir)
            println("BUILT!")
        } catch (DBE: DbException) {
        }
        Caches.clearAllCaches().commit()
        Caches.allCachesList.forEach { assertTrue(it.isEmpty()) }
    }

    @AfterEach
    fun afterEach() {
        println("DELETING...")
        Database.clearAllDBs().commit()
        Caches.clearAllCaches().commit()
        Database.allDBs.forEach { assertTrue(it.count().toInt() == 0) }
        Caches.allCachesList.forEach { assertTrue(it.isEmpty()) }
        println("DELETED!")
    }
}