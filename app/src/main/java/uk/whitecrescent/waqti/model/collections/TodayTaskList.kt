package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.task.Task

class TodayTaskList(tasks: Collection<Task>) : TypedTaskList(tasks) {

    override var list: ArrayList<Task>
        get() = ArrayList()
        set(value) {}
}