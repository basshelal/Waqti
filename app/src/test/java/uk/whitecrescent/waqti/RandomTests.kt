package uk.whitecrescent.waqti

import org.junit.jupiter.api.DisplayName
import uk.whitecrescent.waqti.persistence.BasePersistenceTest

@DisplayName("Random Tests")
class RandomTests : BasePersistenceTest() {

//    @DisplayName("Test")
//    @Test
//    fun test() {
//
//        val x = getTasks(100).onEach { it.apply { setDeadlineConstraintValue(tomorrow at 11) } }
//
//        //Caches.testTaskCache.putAll(x)
//
//        sleep(5)
//
//        println(Caches.testTaskCache.size)
//
//        println("PREFETCHING!")
//        Caches.testTaskCache.prefetchAll(Database.tasks.all.ids, null)
//        println("DONE!")
//        sleep(5)
//
//        println(Caches.testTaskCache.get(50))
//        println(Caches.testTaskCache.asMap().values.toList())
//
//    }

}