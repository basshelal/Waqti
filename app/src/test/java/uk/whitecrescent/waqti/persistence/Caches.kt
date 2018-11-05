package uk.whitecrescent.waqti.persistence

//class TestCacheable(name: String) : Cacheable {
//
//    var name: String = name
//        set(value) {
//            field = value
//            update()
//        }
//
//    override val id = TestCaches.cache.newID()
//
//    init {
//        update()
//    }
//
//    override fun update() {
//        TestCaches.cache.put(this)
//    }
//
//    override fun toString() = "TestCacheable: $name"
//}
//
//class CacheableString(var name: String) : Cacheable {
//
//    override val id = TestCaches.stringCache.newID()
//
//    override fun update() = TestCaches.stringCache.put(this)
//
//    override fun toString() = name
//}
//
//object TestCaches {
//    val cache = Cache<TestCacheable>(null)
//    val stringCache = Cache<CacheableString>(null)
//}