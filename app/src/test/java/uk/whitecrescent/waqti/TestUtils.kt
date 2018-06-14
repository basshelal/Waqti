package uk.whitecrescent.waqti

import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.task.Task

fun testTask() = Task("TestTask")

fun getTasks(amount: Int): List<Task> {
    return (0 until amount).map { Task("TestTask $it") }.toList()
}

inline fun after(duration: Duration, func: () -> Any) {
    Thread.sleep(duration.toMillis())
    func.invoke()
}