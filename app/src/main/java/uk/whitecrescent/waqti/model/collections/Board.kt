package uk.whitecrescent.waqti.model.collections

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.ID

@Entity
class Board(name: String = "", lists: Collection<TaskList> = emptyList())
    : AbstractWaqtiList<TaskList>(), Cacheable {

    @Convert(converter = IDArrayListConverter::class, dbType = String::class)
    override var idList = ArrayList<ID>()

    @Id
    override var id: Long = 0L

    var name: String = name
        set(value) {
            field = value
            update()
        }

    init {
        this.growTo(lists.size)
        this.addAll(lists)
        update()
    }

    override fun getAll(): LinkedHashMap<ID, TaskList> {
        return LinkedHashMap(
                Database.taskLists.all
                        .filter { it.id in idList }
                        .map { it.id to it }
                        .toMap()
        )
    }

    override fun notDefault(): Boolean {
        return this.name != "" || this.id != 0L || this.idList.isNotEmpty()
    }

    override fun update() {
        Caches.boards.put(this)
    }
}