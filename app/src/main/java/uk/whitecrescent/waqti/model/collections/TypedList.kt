package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.task.Task

abstract class TypedList(tasks: Collection<Task>) : BasicList(tasks) {
}