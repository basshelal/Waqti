package uk.whitecrescent.waqti.backend.collections

import io.objectbox.annotation.Transient
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.backend.persistence.Cache
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Task

@ForLater
// Name should change to something like Ordering or Series or Sequence
class Tuple(name: String = "",
            tasks: Collection<Task>) : AbstractWaqtiList<Task>() {

    override var idList = ArrayList<ID>()

    @Transient
    override val cache: Cache<Task> = Caches.tasks

    override var name: String = name
        set(value) {
            field = value
            update()
        }

    init {
        this.growTo(tasks.size)
        this.addAll(tasks.toList())
    }

    override fun initialize() {

    }

    override fun notDefault(): Boolean {
        return false
    }

    override var id: ID = 0

    override fun update() {

    }

    //region Add

    //By default adds all unconstrained
//    @Throws(IndexOutOfBoundsException::class)
//    override fun addAllAt(index: Int, collection: Collection<Task>): Tuple {
//        when {
//            !inRange(index) -> {
//                throw  IndexOutOfBoundsException("Cannot add $collection at index $index, limits are 0 to $nextIndex")
//            }
//            collection.isNotEmpty() -> {
//                val collectionList = collection.toList()
//                if (collectionList.size > 1) {
//                    for (i in 1..collectionList.lastIndex) {
//                        collectionList[i]
//                                .setBeforePropertyValue(collectionList[i - 1])
//                    }
//                }
//                if (this.isNotEmpty()) {
//                    collectionList[0].setBeforePropertyValue(this[index - 1])
//                    if (index < size) {
//                        this[index].setBeforePropertyValue(collectionList.last())
//                    }
//                }
//                list.addAll(index, collectionList)
//            }
//        }
//        return this
//    }

//    @Throws(IndexOutOfBoundsException::class)
//    override fun addAt(index: Int, element: Task): Tuple {
//        if (!inRange(index)) {
//            throw  IndexOutOfBoundsException("Cannot add $element at index $index, limits are 0 to $nextIndex")
//        } else {
//            if (this.isNotEmpty()) {
//                element.setBeforePropertyValue(this[index - 1])
//                if (index < size) {
//                    this[index].setBeforePropertyValue(element)
//                }
//            }
//            list.add(index, element)
//            return this
//        }
//    }

    override fun add(element: Task) = super.add(element) as Tuple

    override fun addAll(collection: Collection<Task>) = super.addAll(collection) as Tuple

    override fun addIf(collection: Collection<Task>, predicate: (Task) -> Boolean) =
            super.addIf(collection, predicate) as Tuple

    //endregion Add

    //region Update

    override fun updateAt(oldIndex: Int, newElement: Task) = super.updateAt(oldIndex, newElement) as Tuple

    override fun updateAllTo(collection: Collection<Task>, new: Task) = super.updateAllTo(collection, new) as Tuple

    override fun update(old: Task, new: Task) = super.update(old, new) as Tuple

    override fun updateIf(predicate: (Task) -> Boolean, new: Task) = super.updateIf(predicate, new) as Tuple

    //endregion Update

    //region Remove

//    override fun removeAt(index: Int): Tuple {
//        when {
//            !inRange(index) -> {
//                throw  IndexOutOfBoundsException("Cannot remove at index $index, limits are 0 to $nextIndex")
//            }
//            this.size == 1 -> {
//                this[index].hideBefore()
//                list.removeAt(index)
//            }
//            index == 0 -> {
//                this[index + 1].hideBefore()
//                list.removeAt(index)
//            }
//            index == size - 1 -> {
//                this[index].hideBefore()
//                list.removeAt(index)
//            }
//            else -> {
//                this[index].hideBefore()
//                this[index + 1].setBeforePropertyValue(this[index - 1])
//                list.removeAt(index)
//            }
//        }
//        return this
//    }
//
//    override fun removeAll(collection: Collection<Task>): Tuple {
//        if (collection.isNotEmpty()) {
//            this.getAll(collection).forEach { it.hideBefore() }
//            list.removeAll(collection)
//            adjust()
//        }
//        return this
//    }

    //endregion Remove

    //region Manipulate

    @Throws(IndexOutOfBoundsException::class)//from super.move
    override fun move(fromIndex: Int, toIndex: Int): Tuple {
        super.move(fromIndex, toIndex)
        adjust()
        return this
    }

    override fun swap(thisIndex: Int, thatIndex: Int) = super.swap(thisIndex, thatIndex) as Tuple

    override fun moveAllTo(collection: Collection<Task>, toIndex: Int) = super.moveAllTo(collection, toIndex) as Tuple

    override fun move(from: Task, to: Task) = super.move(from, to) as Tuple

    override fun swap(`this`: Task, that: Task) = super.swap(`this`, that) as Tuple

    override fun sort(comparator: Comparator<Task>) = super.sort(comparator) as Tuple

    //endregion Manipulate

    //region List Utils

    override fun growTo(size: Int) = super.growTo(size) as Tuple

    //endregion

    fun constrainAt(index: Int): Tuple {
        when {
            !inRange(index) -> {
                throw IndexOutOfBoundsException("Cannot constrain at $index, limits are 0 to $nextIndex")
            }
            this.size > 1 -> {
                this[index].setBeforeConstraintValue(idList[index - 1])
            }
        }
        return this
    }

    @Throws(ElementNotFoundException::class) //from indexOf
    fun constrain(element: Task) = constrainAt(indexOf(this[element]))

    fun constrainAll(): Tuple {
        if (this.size > 1) {
            for (index in 1..size - 1) {
                this[index].setBeforeConstraintValue(this[index - 1])
            }
        }
        return this
    }

    fun unConstrainAt(index: Int): Tuple {
        when {
            !inRange(index) -> {
                throw IndexOutOfBoundsException("Cannot constrain at $index, limits are 0 to $nextIndex")
            }
            this.size > 1 -> {
                this[index].setBeforePropertyValue(idList[index - 1])
            }
        }
        return this
    }

    @Throws(ElementNotFoundException::class) //from indexOf
    fun unConstrain(element: Task) = unConstrainAt(indexOf(this[element]))

    fun unConstrainAll(): Tuple {
        if (this.size > 1) {
            for (index in 1..size - 1) {
                this[index].setBeforePropertyValue(this[index - 1])
            }
        }
        return this
    }

    fun addAndConstrainAt(index: Int, task: Task): Tuple {
        this.addAt(index, task)
        this.constrainAt(index)
        return this
    }

    fun addAndConstrain(task: Task) = addAndConstrainAt(nextIndex, task)

    @Throws(IndexOutOfBoundsException::class)
    fun killTaskAt(index: Int): Tuple {
        when {
            !inRange(index) -> {
                throw IndexOutOfBoundsException("Cannot kill task at $index, limits are 0 to $nextIndex")
            }
            else -> {
                this[index].kill()
            }
        }
        return this
    }

    fun killTask(task: Task): Tuple {
        this[task].kill()
        return this
    }

    private fun adjust() = forEachIndexed { index, task ->
        if (index == 0) {
            task.hideBefore()
        }
        if (index > 0) {
            when {
                task.before.isConstrained -> {
                    task.setBeforeConstraintValue(this[index - 1])
                }
                else -> {
                    task.setBeforePropertyValue(this[index - 1])
                }
            }
        }
    }

    companion object {

        fun fromTuples(tuples: Collection<Tuple>): Tuple {
            val result = Tuple(tasks = emptyList())
            tuples.forEach { result.addAll(it.toList()) }
            return result
        }

        fun fromTuples(vararg tuples: Tuple) = fromTuples(tuples.toList())

    }

}