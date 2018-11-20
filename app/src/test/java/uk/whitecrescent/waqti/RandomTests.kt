package uk.whitecrescent.waqti

import io.objectbox.kotlin.boxFor
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.collections.TestTaskList
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.persistence.BasePersistenceTest

@DisplayName("Random Tests")
class RandomTests : BasePersistenceTest() {

    @DisplayName("Task List")
    @Test
    fun testTaskList() {
        val list = TestTaskList()

        list.addAll(getTasks(100))

        Database.put(list)

        val listDB = Database.store.boxFor<TestTaskList>()[1]
        println(listDB.toList())
        Database.store.boxFor<TestTaskList>().removeAll()
    }

}