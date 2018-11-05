package uk.whitecrescent.waqti.persistence

import io.objectbox.BoxStore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.Database
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.DEBUG
import uk.whitecrescent.waqti.model.task.LabelArrayListProperty
import uk.whitecrescent.waqti.model.task.MyObjectBox
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.TestEntity
import uk.whitecrescent.waqti.model.task.TimeProperty
import java.io.File

@DisplayName("ObjectBox Tests")
class ObjectBoxTests {

    private val directory = File("testDB")
    private lateinit var store: BoxStore

    @BeforeEach
    fun beforeEach() {
        BoxStore.deleteAllFiles(directory)
        store = MyObjectBox.builder()
                .directory(directory)
                .build()
    }

    @AfterEach
    fun afterEach() {
        store.close()
        BoxStore.deleteAllFiles(directory)
    }


    @DisplayName("Test")
    @Test
    fun test() {
        val box = store.boxFor(TestEntity::class.java)

        val list = arrayListOf(
                TestEntity(TimeProperty(true, now)),
                TestEntity(TimeProperty(false, now.plusDays(12)), LabelArrayListProperty(true)),
                TestEntity(TimeProperty(true, now.minusHours(59)))
        )

        box.put(list)

        assertEquals(box.count(), 3)

        assertEquals(list, box.all)

    }

    @DisplayName("Test1")
    @Test
    fun test1() {
        val box = Database.taskDB

        val data = Array<Task>(10_000, { Task() }).toList()

        box.put(data)

        DEBUG = false

        assertEquals(box.count(), 10_000)

        println("Reading at $now")

        Caches.tasks.readAll()

        println("Done at $now")

        println("${Caches.tasks[10_000]} == ${box.get(10_000)}")

        assertEquals(Caches.tasks[10_000], box.get(10_000))

        Database.deleteDB()
    }

}