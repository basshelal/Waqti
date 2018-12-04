@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti

import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Task

fun testTask() = testTask

val testTask: Task
    get() = Task("TestTask")

fun testTask(name: String): Task {
    return Task(name)
}

inline fun getTasks(amount: Int) = Array(amount, { testTask("TestTask # ${it + 1}") }).toList()

inline fun getLabels(amount: Int) = Array(amount, { Label("Label # ${it + 1}") }).toList()

fun after(duration: Duration, func: () -> Any) {
    Thread.sleep(duration.toMillis())
    func.invoke()
}

inline fun sleep(duration: Duration) {
    Thread.sleep(duration.toMillis())
}

inline fun <T> message(expected: T, actual: T) {
    println("Expected:\t${expected.toString()} \nActual:\t\t${actual.toString()}")
}

// Global variable to check if the DB has been built
var DB_BUILT = false