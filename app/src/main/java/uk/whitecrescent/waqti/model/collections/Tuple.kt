package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Listable
import uk.whitecrescent.waqti.model.task.Constraint
import uk.whitecrescent.waqti.model.task.Task

// Name should change to something like Ordering or Series or Sequence
class Tuple(tasks: Collection<Task>) : AbstractWaqtiList<Task>(), Listable {

    constructor(vararg tasks: Task) : this(tasks.toList())

    init {
        this.growTo(tasks.size)
        this.addAll(tasks.toList())
    }

    //region Add

    //By default adds all unconstrained
    @Throws(IndexOutOfBoundsException::class)
    override fun addAllAt(index: Int, collection: Collection<Task>): Tuple {
        when {
            !inRange(index) -> {
                throw  IndexOutOfBoundsException("Cannot add $collection at index $index, limits are 0 to $nextIndex")
            }
            collection.isNotEmpty() -> {
                val collectionList = collection.toList()
                if (collectionList.size > 1) {
                    for (i in 1..collectionList.lastIndex) {
                        collectionList[i]
                                .setBeforePropertyValue(collectionList[i - 1])
                    }
                }
                if (this.isNotEmpty()) {
                    collectionList[0].setBeforePropertyValue(this[index - 1])
                    if (index < size) {
                        this[index].setBeforePropertyValue(collectionList.last())
                    }
                }
                list.addAll(index, collectionList)
            }
        }
        return this
    }

    @Throws(IndexOutOfBoundsException::class)
    override fun addAt(index: Int, element: Task): Tuple {
        if (!inRange(index)) {
            throw  IndexOutOfBoundsException("Cannot add $element at index $index, limits are 0 to $nextIndex")
        } else {
            if (this.isNotEmpty()) {
                element.setBeforePropertyValue(this[index - 1])
                if (index < size) {
                    this[index].setBeforePropertyValue(element)
                }
            }
            list.add(index, element)
            return this
        }
    }

    //Simple Override changes return type to be more specific
    @SimpleOverride
    override fun add(element: Task) = super.add(element) as Tuple

    @SimpleOverride
    override fun addAll(vararg elements: Task) = super.addAll(*elements) as Tuple

    @SimpleOverride
    override fun addAll(collection: Collection<Task>) = super.addAll(collection) as Tuple

    @SimpleOverride
    override fun addAllAt(index: Int, vararg elements: Task) = super.addAllAt(index, *elements) as Tuple

    @SimpleOverride
    override fun addIf(collection: Collection<Task>, predicate: (Task) -> Boolean) =
            super.addIf(collection, predicate) as Tuple

    //endregion Add

    //region Update

    @SimpleOverride
    override fun updateAt(oldIndex: Int, newElement: Task) = super.updateAt(oldIndex, newElement) as Tuple

    @SimpleOverride
    override fun updateAllTo(collection: Collection<Task>, new: Task) = super.updateAllTo(collection, new) as Tuple

    @SimpleOverride
    override fun update(old: Task, new: Task) = super.update(old, new) as Tuple

    @SimpleOverride
    override fun updateIf(predicate: (Task) -> Boolean, new: Task) = super.updateIf(predicate, new) as Tuple

    //endregion Update

    //region Remove

    override fun removeAt(index: Int): Tuple {
        when {
            !inRange(index) -> {
                throw  IndexOutOfBoundsException("Cannot remove at index $index, limits are 0 to $nextIndex")
            }
            this.size == 1 -> {
                this[index].hideBefore()
                list.removeAt(index)
            }
            index == 0 -> {
                this[index + 1].hideBefore()
                list.removeAt(index)
            }
            index == size - 1 -> {
                this[index].hideBefore()
                list.removeAt(index)
            }
            else -> {
                this[index].hideBefore()
                this[index + 1].setBeforePropertyValue(this[index - 1])
                list.removeAt(index)
            }
        }
        return this
    }

    override fun removeAll(collection: Collection<Task>): Tuple {
        if (collection.isNotEmpty()) {
            this.getAll(collection).forEach { it.hideBefore() }
            list.removeAll(collection)
            adjust()
        }
        return this
    }

    @SimpleOverride
    override fun removeFirst(element: Task) = super.removeFirst(element) as Tuple

    @SimpleOverride
    override fun removeAll(vararg elements: Task) = super.removeAll(*elements) as Tuple

    @SimpleOverride
    override fun removeIf(predicate: (Task) -> Boolean) = super.removeIf(predicate) as Tuple

    @SimpleOverride
    override fun clear() = super.clear() as Tuple

    @SimpleOverride
    override fun removeRange(fromIndex: Int, toIndex: Int) = super.removeRange(fromIndex, toIndex) as Tuple

    //endregion Remove

    //region Manipulate

    @Throws(IndexOutOfBoundsException::class)//from super.move
    override fun move(fromIndex: Int, toIndex: Int): Tuple {
        super.move(fromIndex, toIndex)
        adjust()
        return this
    }

    @SimpleOverride
    override fun swap(thisIndex: Int, thatIndex: Int) = super.swap(thisIndex, thatIndex) as Tuple

    @SimpleOverride
    override fun moveAllTo(collection: Collection<Task>, toIndex: Int) = super.moveAllTo(collection, toIndex) as Tuple

    @SimpleOverride
    override fun move(from: Task, to: Task) = super.move(from, to) as Tuple

    @SimpleOverride
    override fun swap(`this`: Task, that: Task) = super.swap(`this`, that) as Tuple

    @SimpleOverride
    override fun moveAllTo(toIndex: Int, vararg elements: Task) = super.moveAllTo(toIndex, *elements) as Tuple

    @SimpleOverride
    override fun sort(comparator: Comparator<Task>) = super.sort(comparator) as Tuple

    //endregion Manipulate

    //region List Utils

    @SimpleOverride
    override fun growTo(size: Int) = super.growTo(size) as Tuple

    //endregion

    fun constrainAt(index: Int): Tuple {
        when {
            !inRange(index) -> {
                throw IndexOutOfBoundsException("Cannot constrain at $index, limits are 0 to $nextIndex")
            }
            this.size > 1 -> {
                list[index].setBeforeConstraintValue(list[index - 1])
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
                list[index].setBeforePropertyValue(list[index - 1])
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
                list[index].kill()
            }
        }
        return this
    }

    fun killTask(task: Task): Tuple {
        this[task].kill()
        return this
    }

    fun toHabit(interval: Duration) = Tuple.toHabit(this, interval)

    private fun adjust() = forEachIndexed { index, task ->
        if (index == 0) {
            task.hideBefore()
        }
        if (index > 0) {
            when {
                task.before is Constraint -> {
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
            val result = Tuple()
            tuples.forEach { result.addAll(it.toList()) }
            return result
        }

        fun fromTuples(vararg tuples: Tuple) = fromTuples(tuples.toList())

        fun toHabit(tuple: Tuple, interval: Duration) = Habit(tuple, interval)
    }

    // TODO: 08-Apr-18 How do we do Routines?? Like Templates??
    object Routine {

        fun fromRoutine() {}

        fun toRoutine(tuple: Tuple) {}

    }

}