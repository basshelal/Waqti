package uk.whitecrescent.waqti

import androidx.annotation.CallSuper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.Database
import java.io.File

abstract class BaseTest {

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
    @CallSuper
    open fun beforeEach() {
        Database.clearAllDBs().commit()
        Caches.clearAllCaches().commit()

        Database.allDBs.forEach { assertTrue(it.isEmpty) }
        Caches.allCaches.forEach { assertTrue(it.isEmpty()) }
    }

    @AfterEach
    @CallSuper
    open fun afterEach() {
        Database.clearAllDBs().commit()
        Caches.clearAllCaches().commit()

        Database.allDBs.forEach { assertTrue(it.isEmpty) }
        Caches.allCaches.forEach { assertTrue(it.isEmpty()) }
    }
}