package uk.whitecrescent.waqti.tests.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.ChronoUnit
import uk.whitecrescent.waqti.model.Date
import uk.whitecrescent.waqti.model.collections.Tuple
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.DEFAULT_BEFORE_PROPERTY
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Timer
import uk.whitecrescent.waqti.model.today
import uk.whitecrescent.waqti.testTask

@DisplayName("Tuple Tests")
class TupleTests {

    private fun testOrdered(tuple: Tuple) =
            (0 until tuple.size - 1).forEach {
                if (it == 0) {
                    assertEquals(DEFAULT_BEFORE_PROPERTY, tuple[it].before)
                }
                assertEquals(tuple[it].taskID, tuple[it + 1].before.value)
            }

    @DisplayName("Tuple creation zero Tasks")
    @Test
    fun testTupleCreationZeroTasks() {
        assertAll({ Tuple() })
        assertTrue(Tuple().isEmpty())
        assertEquals(0, Tuple().size)
    }

    @DisplayName("Tuple creation one Task")
    @Test
    fun testTupleCreationOneTask() {
        val tuple = Tuple(
                Task()
        )
        assertTrue(tuple.isNotEmpty())
        assertEquals(1, tuple.size)
    }

    @DisplayName("Tuple Add Single")
    @Test
    fun testTupleAddSingle() {
        val task = testTask()
        val tuple = Tuple()

        @Suppress("USELESS_IS_CHECK")
        assertTrue(tuple.add(task) is Tuple)

        assertEquals(1, tuple.size)
        assertEquals(task, tuple[0])
        assertFalse(tuple[0].before.isVisible)
    }

    @DisplayName("Tuple Add Again")
    @Test
    fun testTupleAddAgain() {
        val task0 = Task("Task1")
        val task1 = Task("Task2")
        val tuple = Tuple()

        tuple.add(task0).add(task1)

        assertEquals(2, tuple.size)
        assertEquals(task0, tuple[0])
        assertEquals(task1, tuple[1])
        assertFalse(tuple[0].before.isVisible)
        assertTrue(tuple[1].before.isVisible)
        assertEquals(tuple[0].taskID, tuple[1].before.value)
    }

    @DisplayName("Tuple Add At")
    @Test
    fun testTupleAddAt() {
        val task = Task("Task")
        val tuple = Tuple()

        tuple
                .add(Task("Task0"))
                .add(Task("Task1"))
                .add(Task("Task2"))

        assertEquals(3, tuple.size)
        assertFalse(tuple[0].before.isVisible)
        assertTrue(tuple[1].before.isVisible)
        assertTrue(tuple[2].before.isVisible)
        assertEquals(tuple[0].taskID, tuple[1].before.value)
        assertEquals(tuple[1].taskID, tuple[2].before.value)

        tuple.addAt(1, task)

        assertEquals(4, tuple.size)
        assertEquals("Task0", tuple[0].title)
        assertEquals("Task", tuple[1].title)
        assertEquals("Task1", tuple[2].title)
        assertEquals("Task2", tuple[3].title)

        assertFalse(tuple[0].before.isVisible)
        assertTrue(tuple[1].before.isVisible)
        assertTrue(tuple[2].before.isVisible)
        assertTrue(tuple[3].before.isVisible)


        assertEquals(tuple[0].taskID, tuple[1].before.value)
        assertEquals(tuple[1].taskID, tuple[2].before.value)
        assertEquals(tuple[2].taskID, tuple[3].before.value)

//        assertEquals("Task0", tuple[1].before.value.task().title)
//        assertEquals("Task", tuple[2].before.value.task().title)

        assertThrows(IndexOutOfBoundsException::class.java, { tuple.addAt(7, Task()) })
    }

    @DisplayName("Tuple Add All vararg")
    @Test
    fun testTupleAddAllVararg() {
        val tasks = getTasks(5).toTypedArray()
        val tuple = Tuple()

        @Suppress("USELESS_IS_CHECK")
        assertTrue(tuple.addAll(*tasks) is Tuple)

        assertEquals(5, tuple.size)

        testOrdered(tuple)
    }

    @DisplayName("Tuple Add All collection")
    @Test
    fun testTupleAddAllCollection() {
        val tasks = getTasks(5)
        val tuple = Tuple()

        tuple.addAll(tasks)
        assertEquals(5, tuple.size)

        testOrdered(tuple)

        tuple.addAll(getTasks(3))

        testOrdered(tuple)
    }

    @DisplayName("Tuple Add All At Vararg")
    @Test
    fun testTupleAddAllAtVararg() {
        val tuple = Tuple().addAll(getTasks(3))

        assertEquals(3, tuple.size)

        testOrdered(tuple)

        @Suppress("USELESS_IS_CHECK")
        assertTrue(tuple.addAllAt(1, *getTasks(2).toTypedArray()) is Tuple)

        testOrdered(tuple)
    }

    @DisplayName("Tuple Add All At collection")
    @Test
    fun testTupleAddAllAtCollection() {
        val tuple = Tuple().addAll(getTasks(3))

        assertEquals(3, tuple.size)

        testOrdered(tuple)

        tuple.addAllAt(1, getTasks(2))

        testOrdered(tuple)

        assertThrows(IndexOutOfBoundsException::class.java, { tuple.addAllAt(7, getTasks(5)) })
    }

    @DisplayName("Tuple Add If")
    @Test
    fun testTupleAddIf() {
        val tuple = Tuple().addAll(getTasks(3))

        tuple.addIf(
                listOf(
                        Task("My Task"),
                        Task(),
                        Task("Another Task")
                ),
                { it.title.isNotBlank() }
        )

        assertEquals(5, tuple.size)
        testOrdered(tuple)

        tuple.forEach { assertTrue(it.title.isNotBlank()) }
    }

    @DisplayName("Tuple Remove First")
    @Test
    fun testTupleRemoveFirst() {
        val tasks = getTasks(3)
        val tuple = Tuple()
                .addAll(tasks)

        tuple.removeFirst(tasks[1])
        assertEquals(2, tuple.size)
        assertEquals(tasks[0], tuple[0])
        assertEquals(tasks[2], tuple[1])
    }

    @DisplayName("Tuple Remove At")
    @Test
    fun testTupleRemoveAt() {
        val tasks = getTasks(6)
        val tuple = Tuple()
                .addAll(tasks)

        tuple.removeAt(0)
        assertEquals(5, tuple.size)
        testOrdered(tuple)

        tuple.removeAt(4)
        assertEquals(4, tuple.size)
        testOrdered(tuple)

        tuple.removeAt(2)
        assertEquals(3, tuple.size)
        testOrdered(tuple)

        tuple.removeAt(0).removeAt(0)
        assertEquals(1, tuple.size)
        testOrdered(tuple)

        tuple.removeAt(0)
        assertEquals(0, tuple.size)
        assertTrue(tuple.isEmpty())
        testOrdered(tuple)
        tasks.forEach { assertTrue(it.before == DEFAULT_BEFORE_PROPERTY) }
    }

    @DisplayName("Tuple Remove All Vararg")
    @Test
    fun testTupleRemoveAllVararg() {
        val tasks = getTasks(10)
        val tasks0 = tasks.subList(5, 10)
        val tasks1 = tasks.subList(0, 5)
        val tuple = Tuple().addAll(tasks)

        tuple.removeAll(*tasks0.toTypedArray())
        assertEquals(5, tuple.size)
        assertEquals(tasks1[0], tuple[0])
        assertEquals(tasks1[1], tuple[1])
        assertEquals(tasks1[2], tuple[2])
        assertEquals(tasks1[3], tuple[3])
        assertEquals(tasks1[4], tuple[4])

        testOrdered(tuple)
    }

    @DisplayName("Tuple Remove All collection")
    @Test
    fun testTupleRemoveAllCollection() {
        val tasks = getTasks(10)
        val tasks0 = tasks.subList(5, 10)
        val tasks1 = tasks.subList(0, 5)
        val tuple = Tuple().addAll(tasks)

        tuple.removeAll(tasks0)
        assertEquals(5, tuple.size)
        (0..4).forEach { assertEquals(tasks1[it], tuple[it]) }

        testOrdered(tuple)
        tasks0.forEach { assertTrue(it.before == DEFAULT_BEFORE_PROPERTY) }

        val tasks2 = listOf(Task("R1"), Task("R2"), Task("R3"))

        tuple.clear().addAll(tasks2).addAll(Task("K1"), Task("K2"))
        assertEquals(5, tuple.size)

        tuple.removeAll(tasks2)
        assertEquals(2, tuple.size)
        testOrdered(tuple)
    }

    @DisplayName("Tuple Move")
    @Test
    fun testTupleMove() {
        val tuple = Tuple().addAll(getTasks(6))
        tuple.move(1, 3)
        val titles = listOf("TestTask0", "TestTask2", "TestTask3", "TestTask1", "TestTask4", "TestTask5")
        titles.forEachIndexed { index, string -> assertTrue(string == tuple[index].title) }
        testOrdered(tuple)
    }

    @Disabled
    @DisplayName("Test")
    @Test
    fun testTimer() {
        val timer = Timer()

        val startTime = System.currentTimeMillis();timer.start()
        for (i in 0..9) {
            println("My Timer: ${timer.duration.toMillis()}")
            println("System Timer: ${System.currentTimeMillis() - startTime}")
            sleep(60)
        }

        println("END \n\n")
        println("My Timer: ${timer.duration.toMillis()}")
        println("System Timer: ${System.currentTimeMillis() - startTime}")
        timer.stop()
    }

    // For Calendar using Days
    @DisplayName("Test")
    @Test
    fun test() {
        val map = HashMap<Int, Date>()
        map.putAll(dates(today.minusYears(1), today.plusYears(1)))
        //assertTrue(map.size == (731))
        println(map.toList().first())
        println(map.toList().last())
    }


    private fun dates(from: Date, to: Date): HashMap<Int, Date> {
        val period = from.until(to.plusDays(1), ChronoUnit.DAYS)
        val hashMap = HashMap<Int, Date>(period.toInt())
        for (day in 0 until period.toInt()) {
            hashMap[day] = Date.from(from.plusDays(day.toLong()))
        }
        return hashMap
    }

}