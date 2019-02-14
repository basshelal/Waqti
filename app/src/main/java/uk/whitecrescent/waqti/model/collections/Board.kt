package uk.whitecrescent.waqti.model.collections

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import uk.whitecrescent.waqti.android.customview.WaqtiColor
import uk.whitecrescent.waqti.android.customview.WaqtiColorConverter
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.persistence.Cache
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

@Entity
class Board(name: String = "", lists: Collection<TaskList> = emptyList())
    : AbstractWaqtiList<TaskList>(), Cacheable {

    @Convert(converter = IDArrayListConverter::class, dbType = String::class)
    override var idList = ArrayList<ID>()

    @Transient
    override val cache: Cache<TaskList> = Caches.taskLists

    @Id
    override var id: Long = 0L

    var name: String = name
        set(value) {
            field = value
            update()
        }

    @Convert(converter = WaqtiColorConverter::class, dbType = String::class)
    var backgroundColor: WaqtiColor = WaqtiColor.DEFAULT
        set(value) {
            field = value
            update()
        }

    @Convert(converter = WaqtiColorConverter::class, dbType = String::class)
    var cardColor: WaqtiColor = WaqtiColor.CARD_DEFAULT
        set(value) {
            field = value
            update()
        }

    init {
        if (this.notDefault()) {
            this.growTo(lists.size)
            this.addAll(lists)
            this.update()
            this.initialize()
        }
    }

    override fun clear(): AbstractWaqtiList<TaskList> {
        forEach { it.clear().update() }
        val toRemove = this.toList()
        Caches.taskLists.remove(toRemove)
        return super.clear()
    }

    override fun removeAt(index: Int): AbstractWaqtiList<TaskList> {
        val listToRemove = this[index]
        listToRemove.clear().update()
        Caches.taskLists.remove(listToRemove)
        return super.removeAt(index) as Board
    }

    override fun move(fromIndex: Int, toIndex: Int): AbstractWaqtiList<TaskList> {
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

    override fun initialize() {

    }

    override fun notDefault(): Boolean {
        return this.name != "" || this.id != 0L || this.idList.isNotEmpty()
    }

    override fun update() {
        Caches.boards.put(this)
    }
}