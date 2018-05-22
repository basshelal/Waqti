package uk.whitecrescent.waqti.collections

import io.reactivex.Observable
import uk.whitecrescent.waqti.task.ObserverException
import uk.whitecrescent.waqti.task.TIME_CHECKING_PERIOD
import uk.whitecrescent.waqti.task.TIME_CHECKING_UNIT
import uk.whitecrescent.waqti.task.Task
import uk.whitecrescent.waqti.task.TaskState

open class BasicList(tasks: Collection<Task>) : AbstractWaqtiList<Task>() {

    init {
        this.growTo(tasks.size)
        this.addAll(tasks)
    }

    var observingKilled = false

    fun add(collection: Collection<Tuple>): BasicList {
        collection.forEach { this.addAll(it.toList()) }
        return this
    }

    fun add(vararg tuples: Tuple) = add(tuples.toList())

    fun sortByTime(): BasicList {
        this.sort(Comparator { t1, t2 -> t1.time.value.compareTo(t2.time.value) })
        return this
    }

    fun sortByDuration(): BasicList {
        this.sort(Comparator { t1, t2 -> t1.duration.value.compareTo(t2.duration.value) })
        return this
    }

    fun sortByPriority(): BasicList {
        this.sort(Comparator { t1, t2 ->
            t1.priority.value.importanceLevel.compareTo(t2.priority.value.importanceLevel)
        })
        return this
    }

    fun sortByDeadline(): BasicList {
        this.sort(Comparator { t1, t2 -> t1.deadline.value.compareTo(t2.deadline.value) })
        return this
    }

    @Throws(IndexOutOfBoundsException::class)
    fun killAndRemove(index: Int): BasicList {
        if (!inRange(index)) {
            throw IndexOutOfBoundsException("Cannot kill at $index, limits are 0 to $nextIndex")
        } else {
            this[index].kill()
            this.removeAt(index)
            return this
        }
    }

    fun hasKilledTasks() = this.any { it.state == TaskState.KILLED }

    fun removeKilledTasks() = this.removeIf { it.state == TaskState.KILLED }

    // We can make the killed Tasks go to another List as well
    fun autoRemoveKilled(): BasicList {
        observingKilled = true
        Observable.interval(TIME_CHECKING_PERIOD, TIME_CHECKING_UNIT)
                .takeWhile { observingKilled }
                .subscribeOn(LIST_OBSERVER_THREAD)
                .subscribe(
                        {
                            if (this.hasKilledTasks()) {
                                this.removeKilledTasks()
                            }
                        },
                        {
                            throw ObserverException("Concurrent List Observing failed!")
                        }
                )
        return this
    }
}