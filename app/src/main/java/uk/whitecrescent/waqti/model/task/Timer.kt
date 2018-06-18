package uk.whitecrescent.waqti.model.task

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.till
import java.util.concurrent.TimeUnit

// TODO: 14-May-18 Does a Timer need a Task to exist?
/*
 * Tested thoroughly and works to 0.01 seconds accuracy, tested up to an hour (3600 seconds),
 * expected 3600 seconds, actually was 3599.964 seconds, similar results for shorter time periods
 * so I believe this is just some uncontrolled overhead
 *
 */
class Timer {

    private val thread = Schedulers.newThread()
    private val timePeriod = 10L
    private val timeUnit = TimeUnit.MILLISECONDS

    var stopped = true
        private set
    var paused = false
        private set
    var running = false
        private set

    private var lastTime = now

    var duration: Duration = Duration.ZERO
        private set

    private val timer = Observable
            .interval(timePeriod, timeUnit)
            .subscribeOn(thread)
            .takeWhile { running }

    fun start() {
        if (paused || stopped) lastTime = now
        running = true
        paused = false
        stopped = false
        timer.subscribe(
                {
                    duration += (lastTime till now)
                    lastTime = now
                },
                {
                    throw  ObserverException("Timer Failed!")
                }
        )
    }

    fun pause() {
        paused = true
        running = false
        stopped = false
    }

    fun stop() {
        running = false
        stopped = true
        paused = false
        duration = Duration.ZERO
    }
}