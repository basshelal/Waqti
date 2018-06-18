@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti

import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.task.Task

fun testTask() = Task("TestTask")

val testTask: Task
    get() = Task("TestTask")

fun getTasks(amount: Int): List<Task> {
    return (0 until amount).map { Task("TestTask $it") }.toList()
}

inline fun after(duration: Duration, func: () -> Any) {
    Thread.sleep(duration.toMillis())
    func.invoke()
}

inline fun <T> message(expected: T, actual: T) {
    println("Expected:\t${expected.toString()} \nActual:\t\t${actual.toString()}")
}