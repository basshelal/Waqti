package uk.whitecrescent.waqti.model.collections

import io.reactivex.Observable
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Time
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.task.ObserverException
import uk.whitecrescent.waqti.model.task.TIME_CHECKING_PERIOD
import uk.whitecrescent.waqti.model.task.TIME_CHECKING_UNIT
import uk.whitecrescent.waqti.model.task.Timer


class Habit(var tuple: Tuple, var interval: Duration) {

    val startTime: Time
    private val list = ArrayList<Pair<Time, Boolean>>()
    val timer = Timer()

    init {
        startTime = now
        observe()
    }

    private fun observe() {
        Observable.interval(TIME_CHECKING_PERIOD, TIME_CHECKING_UNIT)
                .takeWhile { true }
                .subscribeOn(HABIT_OBSERVER_THREAD)
                .doOnSubscribe { timer.start() }
                .subscribe(
                        {
                            if (timer.duration >= interval) {
                                list.add(now to false)
                                timer.stop()
                                timer.start()
                            }
                            /*
                             * assert that the list has the size of
                             * since the startTime, every interval
                             * so like startTime is 19:00 and interval is 2 hours
                             * then by tomorrow 21:30 the list will have size
                             * 13 so 12 at 19:00 tomorrow and by 21:30 13
                             *
                             */
                        },
                        {
                            throw ObserverException("Habit Checking failed!")
                        }
                )
    }

    fun getList() = list.toList()
}