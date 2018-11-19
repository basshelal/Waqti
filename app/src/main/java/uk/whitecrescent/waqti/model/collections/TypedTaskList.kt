package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.task.Task

abstract class TypedTaskList(tasks: Collection<Task>) : TaskList(tasks) {
}