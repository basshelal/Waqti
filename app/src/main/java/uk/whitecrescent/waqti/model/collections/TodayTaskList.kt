package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

class TodayTaskList(tasks: Collection<Task>) : TypedTaskList(tasks) {

    override var idList = ArrayList<ID>()

}