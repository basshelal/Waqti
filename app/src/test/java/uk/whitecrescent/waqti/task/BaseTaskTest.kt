package uk.whitecrescent.waqti.task

import io.objectbox.exception.DbException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import java.io.File

open class BaseTaskTest {

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
        Caches.allCachesList.forEach { Assertions.assertTrue(it.isEmpty()) }
    }

    @AfterEach
    fun afterEach() {
        println("DELETING...")
        Database.clearAllDBs().commit()
        Caches.clearAllCaches().commit()
        Database.allDBs.forEach { Assertions.assertTrue(it.count().toInt() == 0) }
        Database.allDBs.forEach { it.closeThreadResources() }
        Caches.allCachesList.forEach { Assertions.assertTrue(it.isEmpty()) }
        println("DELETED!")
    }

//    @BeforeEach
//    open fun beforeEach() {
//        Database.buildTest(File("DEBUG_DB"))
//        Caches.allCachesList
//        Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
//    }
//
//    @AfterEach
//    open fun afterEach() {
//        Database.store.close()
//        Database.store.deleteAllFiles()
//        Caches.clearAllTaskCaches().commit()
//        Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
//    }

}