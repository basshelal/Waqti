package uk.whitecrescent.waqti.persistence

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.persistence.Cache

class TestCacheable(name: String) : Cacheable {

    var name: String = name
        set(value) {
            field = value
            update()
        }

    override val id = TestCaches.testCache.newID()

    init {
        update()
    }

    override fun update() {
        TestCaches.testCache.put(this)
    }

}

object TestCaches {
    val testCache = Cache<TestCacheable>()
}