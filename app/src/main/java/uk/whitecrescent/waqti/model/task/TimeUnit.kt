package uk.whitecrescent.waqti.model.task

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.Duration

class TimeUnit(val name: String, val duration: Duration) : Cacheable {

    companion object {

        val allTimeUnits = ArrayList<TimeUnit>()

        fun toJavaDuration(timeUnit: TimeUnit, count: Int): Duration {
            return timeUnit.duration.multipliedBy(count.toLong())
        }

        fun getOrCreateTimeUnit(name: String, duration: Duration): TimeUnit {
            val newTimeUnit = TimeUnit(name, duration)
            val found = allTimeUnits.find { it == newTimeUnit }

            if (found == null) {
                allTimeUnits.add(newTimeUnit)
                return newTimeUnit
            } else return found
        }

        fun getTimeUnit(name: String, duration: Duration): TimeUnit {
            val newTimeUnit = TimeUnit(name, duration)
            val found = allTimeUnits.find { it == newTimeUnit }

            if (found == null) {
                throw IllegalArgumentException("TimeUnit not found")
            } else return found
        }

        fun deleteTimeUnit(name: String, duration: Duration) {
            allTimeUnits.remove(getTimeUnit(name, duration))
        }

    }

    override fun id(): ID {
        return System.currentTimeMillis()
    }

    override fun hashCode() = name.hashCode() + duration.hashCode()

    override fun equals(other: Any?) =
            other is TimeUnit && other.name.equals(this.name) && other.duration == this.duration

    override fun toString() = "$name $duration"
}