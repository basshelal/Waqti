package uk.whitecrescent.waqti

import uk.whitecrescent.waqti.task.Task
import java.time.Duration

fun testTask() = Task("TestTask")

fun getTasks(amount: Int): List<Task> {
    return (0 until amount).map { Task("TestTask $it") }.toList()
}

inline fun after(duration: Duration, func: () -> Any) {
    Thread.sleep(duration.toMillis())
    func.invoke()
}