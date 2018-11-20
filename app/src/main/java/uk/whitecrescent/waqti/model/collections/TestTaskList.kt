package uk.whitecrescent.waqti.model.collections

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

@Entity
class TestTaskList(tasks: Collection<Task> = emptyList()) : AbstractWaqtiList<ID>(), Cacheable {

    /*
     * So, we need to make the TaskList contain the IDs of the Tasks in it, literally all it will
     * contain is IDs (Longs) and that's it, if we must do some manipulation we could probably
     * just do it with the IDs, we could have one function that returns all the Tasks by reading
     * the Database and getting all the Tasks with the given IDs.
     *
     * A problem arises with referencing Tasks. A Task should be able to belong to multiple
     * Lists, at least that's something we don't plan on not having, so we have the problem of,
     * when we delete a Task in one list we should be able to have the option of COMPLETELY
     * deleting it, this would require us to, inside Task, reference the Lists that it belongs
     * to, that way when we delete we can go to those Lists and remove it from there. We
     * reference the Lists by their IDs. So Lists should be Cacheable.
     *
     */

    @Id
    override var id: Long = 0L

    @Convert(converter = IDArrayListConverter::class, dbType = String::class)
    override var list = ArrayList<ID>()

    init {
        this.addAll(tasks.ids)
    }

    // TODO: 19-Nov-18 What to do here??
    override fun notDefault(): Boolean {
        return false
    }

    override fun update() {
        Database.put(this)
    }
}