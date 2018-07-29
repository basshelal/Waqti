package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.model.persistence.Caches

open class BaseTaskTest {

    companion object {

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Caches.clearAllTaskCaches().commit()
            Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            Caches.clearAllTaskCaches().commit()
            Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
        }
    }

    @BeforeEach
    open fun beforeEach() {
        Caches.clearAllTaskCaches().commit()
        Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
    }

    @AfterEach
    open fun afterEach() {
        Caches.clearAllTaskCaches().commit()
        Caches.allTaskCachesList.forEach { assertTrue(it.isEmpty()) }
    }

}