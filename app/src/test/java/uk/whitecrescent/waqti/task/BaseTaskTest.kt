package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import java.io.File

open class BaseTaskTest {

    @BeforeEach
    open fun beforeEach() {
        Database.buildTest(File("DEBUG_DB"))
        Caches.allCachesList
        Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
    }

    @AfterEach
    open fun afterEach() {
        Database.store.close()
        Database.store.deleteAllFiles()
        Caches.clearAllTaskCaches().commit()
        Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
    }

}