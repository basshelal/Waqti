package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.ForLater
import uk.whitecrescent.waqti.model.persistence.Cache
import uk.whitecrescent.waqti.model.task.ID

@ForLater
// A generic list of Boards, we'll use this to contain all Boards in BoardListFragment and the Grid's adapter
class BoardList : AbstractWaqtiList<Board>() {

    override var idList: ArrayList<ID>
        get() = TODO("not implemented")
        set(value) {}

    override val cache: Cache<Board>
        get() = TODO("not implemented")

    override val id: ID
        get() = TODO("not implemented")

    override fun notDefault(): Boolean {
        TODO("not implemented")
    }

    override fun update() {
        TODO("not implemented")
    }

    override fun initialize() {
        TODO("not implemented")
    }


}