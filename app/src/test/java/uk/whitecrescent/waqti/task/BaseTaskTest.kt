package uk.whitecrescent.waqti.task

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import uk.whitecrescent.waqti.model.persistence.Caches

open class BaseTaskTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Caches.allTaskCachesList.forEach { assert(it.isEmpty()) }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            Caches.allTaskCachesList.forEach { assert(it.isEmpty()) }
        }

        @JvmStatic
        @AfterClass
        fun afterClass() {
        }
    }

    @BeforeEach
    open fun beforeEach() {
        Caches.clearAllTaskCaches().commit()
        Caches.allTaskCachesList.forEach { assert(it.isEmpty()) }
    }

    @AfterEach
    open fun afterEach() {
        Caches.clearAllTaskCaches().commit()
        Caches.allTaskCachesList.forEach { assert(it.isEmpty()) }
    }

}