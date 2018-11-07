package uk.whitecrescent.waqti.model.task

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.hash
import uk.whitecrescent.waqti.model.persistence.Caches

@Entity
class TimeUnit(name: String, duration: Duration) : Cacheable {

    @Id
    override var id = 0L

    init {
        update()
    }

    // TODO: 19-Jun-18 more tests to check for the mutability and other things

    var name = name
        set(value) {
            field = value
            update()
        }

    @Convert(converter = DurationConverter::class, dbType = String::class)
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

    override fun update() = Caches.timeUnits.put(this)

    operator fun component1() = name

    operator fun component2() = duration

    override fun hashCode() = hash(name, duration)

    override fun equals(other: Any?) =
            other is TimeUnit &&
                    other.name == this.name &&
                    other.duration == this.duration

    override fun toString() = "$name $duration"
}