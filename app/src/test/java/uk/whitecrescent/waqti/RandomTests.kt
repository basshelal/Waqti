package uk.whitecrescent.waqti

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.at
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.tomorrow
import uk.whitecrescent.waqti.persistence.BasePersistenceTest

@DisplayName("Random Tests")
class RandomTests : BasePersistenceTest() {

    @DisplayName("Test")
    @Test
    fun test() {

        val x = getTasks(100).onEach { it.apply { setDeadlineConstraintValue(tomorrow at 11) } }

        //Caches.testTaskCache.putAll(x)

        sleep(5)

        println(Caches.testTaskCache.size)

        println("PREFETCHING!")
        Caches.testTaskCache.prefetchAll(Database.tasks.all.ids, null)
        println("DONE!")
        sleep(5)

        println(Caches.testTaskCache.get(50))
        println(Caches.testTaskCache.asMap().values.toList())

    }

}