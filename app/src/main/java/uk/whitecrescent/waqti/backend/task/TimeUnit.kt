package uk.whitecrescent.waqti.backend.task

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.backend.Cacheable
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.hash

@Entity
class TimeUnit(name: String = "", duration: Duration = Duration.ZERO) : Cacheable {

    @Id
    override var id = 0L

    // TODO: 19-Jun-18 more tests to check for the mutability and other things

    override var name = name
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

    init {
        if (this.notDefault()) this.update()
    }

    operator fun times(count: Int): Duration {
        return this.duration.multipliedBy(count.toLong())
    }

    override fun initialize() {

    }

    override fun notDefault(): Boolean {
        return name != "" || duration != Duration.ZERO
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