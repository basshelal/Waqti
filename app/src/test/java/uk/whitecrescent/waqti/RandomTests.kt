package uk.whitecrescent.waqti

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.DEBUG
import uk.whitecrescent.waqti.persistence.BasePersistenceTest

@DisplayName("Random Tests")
class RandomTests : BasePersistenceTest() {

    @DisplayName("Test")
    @Test
    fun test() {

        DEBUG = false

        getTasks(100)

        Caches.testTaskCache.prefetchAll(Database.tasks.all.ids, null)

        sleep(2)

        println(Caches.testTaskCache.get(50))
        println(Caches.testTaskCache.asMap().values.toList())

    }

}