package uk.whitecrescent.waqti.model.collections

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
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

    init {
        if (this.notDefault()) {
            this.growTo(lists.size)
            this.addAll(lists)
            this.update()
            this.initialize()
        }
    }

    override fun removeAt(index: Int): AbstractWaqtiList<TaskList> {
        val listToRemove = this[index]
        val tasksToRemove = Caches.tasks.get(listToRemove.toList())
        listToRemove.removeAll()
        Caches.taskLists.remove(listToRemove)
        Caches.tasks.remove(tasksToRemove)
        return super.removeAt(index)
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