package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.Time
import uk.whitecrescent.waqti.model.at
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.seconds
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.tomorrow
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask

@DisplayName("Task Cache Tests")
class TaskCache {

    @BeforeEach
    fun beforeEach() {
        Caches.tasks.clear()
        assertTrue(Caches.tasks.isEmpty())
    }

    @AfterEach
    fun afterEach() {
        Caches.tasks.clear()
        assertTrue(Caches.tasks.isEmpty())
    }

    @DisplayName("Automatically put new Tasks")
    @Test
    fun testAutomaticallyPutNewTasks() {

        getTasks(5)
        assertEquals(5, Caches.tasks.size)

    }

    @DisplayName("Automatically update Tasks")
    @Test
    fun testAutomaticallyUpdateTasks() {
        val task0 = testTask
        val task1 = testTask
        val task2 = testTask

        task0.title = "0"
        task1.title = "1"
        task2.title = "2"

        assertEquals(3, Caches.tasks.size)

        sleep(1.seconds)

        task1.title = "Updated"
        task1.setTargetPropertyValue("New Target")

        sleep(1.seconds)

        assertEquals("Updated", Caches.tasks[task1.id].title)
        assertEquals("New Target", Caches.tasks[task1.id].target.value)
    }

    @DisplayName("Remove from Cache")
    @Test
    fun testRemoveFromCache() {
        //small number for console, but works with big numbers
        val list = getTasks(5)
        assertEquals(5, Caches.tasks.size)

        sleep(1.seconds)

        Caches.clearAllTaskCaches().commit()
        assertEquals(0, Caches.tasks.size)

        sleep(1.seconds)

        list.forEach { assertTrue(it.activeObservers.isEmpty()) }
    }

    @DisplayName("Automatically end observers after removal from Cache")
    @Test
    fun testEndObserversAfterRemoveFromCache0() {
        val list = getTasks(5).onEach { it.setTimeConstraintValue(tomorrow at 15) }
        assertEquals(5, Caches.tasks.size)

        Caches.tasks.forEach { assertTrue(it.getAllUnmetAndShowingConstraints().isNotEmpty()) }

        sleep(1.seconds)

        Caches.tasks.forEach { assertTrue(it.activeObservers.isNotEmpty()) }

        Caches.clearAllTaskCaches().commit()

        sleep(1.seconds)

        list.forEach { assertTrue(it.activeObservers.isEmpty()) }

    }

    @DisplayName("fff")
    @Test
    fun test() {
        val list = listOf(
                Property(true, Time.MIN),
                Property(true, Time.now()),
                Property(true, Time.MAX)
        )

        val time = Time.MIN

        println(list)
        println()
        println(bite(list))
        println()
        println(String(bite(list)))

        println(time.toString().toByteArray())
        println(String(time.toString().toByteArray()))

        /*
         * So for ObjectBox Persistence and probably most other persistence solutions, we need to
         * convert any non primitive types to primitive types or String, this includes our own
         * stuff like Property and Label to given types like Time.
         *
         * Even a list, we can just save it as a String, as long as we define the converter,
         * similar to what GSON does for you out of the box except we will probably have to do
         * this manually. Perhaps this is the only major disadvantage, is that we have to write
         * the converter between the types, however if we do this for every property in something
         * like Task we wouldn't need to create a Task to Primitive converter since it contains
         * purely primitives (at least I think this is true, big if true).
         *
         * That's of course if we use ObjectBox, or we're forced to do it like this
         *
         * 05-Aug-18 B.Helal
         *
         * */

    }


    fun <T> bite(list: List<T>): ByteArray {
        return list.toString().toByteArray()
    }

}