package uk.whitecrescent.waqti.model.collections

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task
import java.util.concurrent.ConcurrentHashMap

@Entity
class TestTaskList(tasks: Collection<Task> = emptyList()) : AbstractWaqtiList<Task>(), Cacheable {

    @Id
    override var id: Long = 0L

    @Convert(converter = IDArrayListConverter::class, dbType = String::class)
    override var idList = ArrayList<ID>()

    init {
        this.addAll(tasks)
    }

    override fun getAll(): ConcurrentHashMap<ID, Task> {
        return ConcurrentHashMap(
                Database.tasks.all
                        .filter { it.id in idList }
                        .map { Pair(it.id, it) }
                        .toMap()
        )
    }

    // TODO: 19-Nov-18 What to do here??
    override fun notDefault(): Boolean {
        return false
    }

    override fun update() {
        Database.put(this)
    }
}