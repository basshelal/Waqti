package uk.whitecrescent.waqti.persistence

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.MyObjectBox
import uk.whitecrescent.waqti.model.task.Task
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


    @DisplayName("Test1")
    @Test
    fun test1() {
        val box = store.boxFor<Task>()

        val data = Array<Task>(10_000, { Task() }).toList()

        box.put(data)

        assertEquals(box.count(), 10_000)

        println("Reading at $now")

        //Caches.tasks.updateMap()

        println("Done at $now")

        println("${Caches.tasks[10_000]} == ${box.get(10_000)}")

        assertEquals(Caches.tasks[10_000], box.get(10_000))

        //Database.deleteDB()
    }

}