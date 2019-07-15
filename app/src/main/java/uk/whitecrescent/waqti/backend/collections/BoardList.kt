package uk.whitecrescent.waqti.backend.collections

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import uk.whitecrescent.waqti.backend.persistence.Cache
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID

@Entity
class BoardList(name: String = "") : AbstractWaqtiList<Board>() {

    @Convert(converter = IDArrayListConverter::class, dbType = String::class)
    override var idList: ArrayList<ID> = ArrayList()

    @Transient
    override val cache: Cache<Board> = Caches.boards

    @Id
    override var id: ID = 0L

    override var name: String = name
        set(value) {
            field = value
            update()
        }

    init {
        if (this.notDefault()) {
            this.update()
            this.initialize()
        }
    }

    override fun removeAt(index: Int): AbstractWaqtiList<Board> {
        val boardToRemove = this[index]
        boardToRemove.clear().update()
        Caches.boards.remove(boardToRemove)
        return super.removeAt(index) as BoardList
    }

    override fun move(fromIndex: Int, toIndex: Int): AbstractWaqtiList<Board> {
        when {
            !inRange(toIndex, fromIndex) -> {
                throw  IndexOutOfBoundsException("Cannot move $fromIndex  to $toIndex, limits are 0 and $nextIndex")
            }
            else -> {
                val found = this[fromIndex]
                super.removeAt(fromIndex)
                super.addAt(toIndex, found)
                return this
            }
        }
    }

    override fun notDefault(): Boolean {
        return this.name != "" || this.id != 0L || this.idList.isNotEmpty()
    }

    override fun update() {
        Caches.boardLists.put(this)
    }

    override fun initialize() {

    }

}