package uk.whitecrescent.waqti.model.task

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.hash
import uk.whitecrescent.waqti.model.persistence.Caches

class TimeUnit(name: String, duration: Duration) : Cacheable {

    override val id = Caches.timeUnits.newID()

    init {
        Caches.timeUnits.put(this)
    }

    // TODO: 19-Jun-18 more tests to check for the mutability and other things

    var name = name
        set(value) {
            field = value
            update()
        }

    var duration = duration
        set(value) {
            field = value
            update()
        }

    companion object {

        fun toJavaDuration(timeUnit: TimeUnit, count: Int): Duration {
            return timeUnit.duration.multipliedBy(count.toLong())
        }

    }

    private fun update() = Caches.timeUnits.put(this)

    operator fun component1() = name

    operator fun component2() = duration

    override fun hashCode() = hash(name, duration)

    override fun equals(other: Any?) =
            other is TimeUnit &&
                    other.name == this.name &&
                    other.duration == this.duration

    override fun toString() = "$name $duration"
}