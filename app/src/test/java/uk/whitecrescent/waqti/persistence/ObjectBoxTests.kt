package uk.whitecrescent.waqti.persistence

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.MyObjectBox
import uk.whitecrescent.waqti.model.task.Priority
import java.io.File

@DisplayName("ObjectBox Tests")
class ObjectBoxTests {

    private val directory = File("/test-db")
    private lateinit var store: BoxStore

    fun setUp() {
        // delete database files before each test to start with a clean database
        BoxStore.deleteAllFiles(directory)
        store = MyObjectBox.builder()
                .directory(directory)
                // optional: add debug flags for more detailed ObjectBox log output
                .debugFlags(DebugFlags.LOG_QUERIES)
                .build()
    }


    fun tearDown() {
        store.close()
        BoxStore.deleteAllFiles(directory)
    }


    @DisplayName("Test")
    @Test
    fun test() {
        setUp()
        val box = store.boxFor(Priority::class.java)

        box.put(Priority("TEST", 5))
        box.put(Priority("TEST", 5))
        box.put(Priority("TEST", 5))

        assertEquals(box.count(), 3)
        println(box.all)

        tearDown()
    }

}