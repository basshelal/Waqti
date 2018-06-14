package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.TemporalUnit
import uk.whitecrescent.waqti.model.Time
import uk.whitecrescent.waqti.model.now

class Calendar private constructor() {

    companion object {
        // TODO: 15-Apr-18 Come back to this
        fun get(period: Long, unit: TemporalUnit, from: Time = now): HashMap<Int, Time> {
            val hashMap = HashMap<Int, Time>(period.toInt())
            val duration = Duration.of(period, unit)
            val beginning = Time.from(from.minus(duration))
            val end = Time.from(from.plus(duration))
            for (current in -period.toInt()..0) {
                hashMap[current] = Time.from(from.minus(current.toLong(), unit))
            }
            for (current in 0..period.toInt()) {
                hashMap[current] = Time.from(from.plus(current.toLong(), unit))
            }
            println(beginning)
            println(hashMap.toList().first().second)
            println()
            println(end)
            println(hashMap.toList().last().second)
            return hashMap
        }
    }

}