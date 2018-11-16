@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.model

// TODO: 15-Nov-18 It would be cool to publish this as a separate thing, test it properly first ofcourse

// Type Aliases for which Time API to use

typealias Time = org.threeten.bp.LocalDateTime
typealias Duration = org.threeten.bp.Duration
typealias Date = org.threeten.bp.LocalDate
typealias ZonedTime = org.threeten.bp.ZonedDateTime
typealias Temporal = org.threeten.bp.temporal.Temporal
typealias TemporalAmount = org.threeten.bp.temporal.TemporalAmount
typealias TemporalUnit = org.threeten.bp.temporal.TemporalUnit
typealias LocalTime = org.threeten.bp.LocalTime
typealias ChronoUnit = org.threeten.bp.temporal.ChronoUnit
typealias ChronoField = org.threeten.bp.temporal.ChronoField
typealias Instant = org.threeten.bp.Instant
typealias DayOfWeek = org.threeten.bp.DayOfWeek
typealias Year = org.threeten.bp.Year
typealias YearMonth = org.threeten.bp.YearMonth
typealias Month = org.threeten.bp.Month
typealias MonthDay = org.threeten.bp.MonthDay

// Useful Extensions for java.time to make code readable and concise

inline val now: Time
    get() = Time.now()

inline val today: Date
    get() = Date.now()

inline val tomorrow: Date
    get() = Date.now().plusDays(1)

inline val Number.millis: Duration
    get() = Duration.ofMillis(this.toLong())

inline val Number.seconds: Duration
    get() = Duration.ofSeconds(this.toLong())

inline val Number.minutes: Duration
    get() = Duration.ofMinutes(this.toLong())

inline val Number.hours: Duration
    get() = Duration.ofHours(this.toLong())

inline val Number.days: Duration
    get() = Duration.ofDays(this.toLong())

inline val Number.weeks: Duration
    get() = Duration.ofDays(7L * this.toLong())

inline val Number.am: LocalTime
    get() = LocalTime.of(this.toInt(), 0)

inline val Number.pm: LocalTime
    get() = LocalTime.of(this.toInt() + 12, 0)

inline val Pair<Number, Number>.am: LocalTime
    get() = LocalTime.of(this.first.toInt(), this.second.toInt())

inline val Pair<Number, Number>.pm: LocalTime
    get() = LocalTime.of(this.first.toInt() + 12, this.second.toInt())

inline val Triple<Number, Number, Number>.am: LocalTime
    get() = LocalTime.of(this.first.toInt(), this.second.toInt(), this.third.toInt())

inline val Triple<Number, Number, Number>.pm: LocalTime
    get() = LocalTime.of(this.first.toInt() + 12, this.second.toInt(), this.third.toInt())

inline val Duration.millis: Long
    get() = this.toMillis()

inline val Duration.secs: Double
    get() {
        return (this.millis) / 1000.0
    }

inline fun time(year: Int, month: Int, dayOfMonth: Int,
                hour: Int = 0, minute: Int = 0, second: Int = 0, nanoOfSecond: Int = 0) =
        Time.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)

inline fun time(year: Int, month: Month, dayOfMonth: Int,
                hour: Int = 0, minute: Int = 0, second: Int = 0, nanoOfSecond: Int = 0) =
        Time.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)

inline fun time(hour: Number, minute: Number): Pair<Number, Number> = Pair(hour, minute)

inline fun time(hour: Number, minute: Number, second: Number): Triple<Number, Number, Number> =
        Triple(hour, minute, second)

inline infix fun Date.at(time: LocalTime): Time = this.atTime(time)

inline infix fun Date.at(hour: Int): Time = this.atTime(hour, 0)

inline infix fun Date.at(pair: Pair<Number, Number>): Time =
        this.atTime(pair.first.toInt(), pair.second.toInt())

inline infix fun Date.at(triple: Triple<Number, Number, Number>): Time =
        this.atTime(triple.first.toInt(), triple.second.toInt(), triple.third.toInt())

inline infix fun Number.colon(other: Number): Pair<Number, Number> = this to other

inline infix fun Temporal.till(other: Temporal): Duration = Duration.between(this, other)

inline infix fun Duration.from(temporalAmount: TemporalAmount): Duration = Duration.from(temporalAmount)

inline fun coming(dayOfWeek: DayOfWeek): Date {
    return Date.from(today.dayOfWeek + dayOfWeek.value.toLong())
}

inline fun last(dayOfWeek: DayOfWeek): Date {
    return Date.from(today.dayOfWeek - dayOfWeek.value.toLong())
}