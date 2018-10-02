package uk.whitecrescent.waqti.persistence

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.persistence.Cache

class TestCacheable(name: String) : Cacheable {

    var name: String = name
        set(value) {
            field = value
            update()
        }

    override val id = TestCaches.cache.newID()

    init {
        update()
    }

    override fun update() {
        TestCaches.cache.put(this)
    }

    override fun toString() = "TestCacheable: $name"
}

class CacheableString(var name: String) : Cacheable {

    override val id = TestCaches.stringCache.newID()

    override fun update() = TestCaches.stringCache.put(this)

    override fun toString() = name
}

object TestCaches {
    val cache = Cache<TestCacheable>()
    val stringCache = Cache<CacheableString>()
}