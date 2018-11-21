package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.task.ID

class TodayTaskList(name: String) : TypedTaskList(name) {

    override var idList = ArrayList<ID>()

}