package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.message
import uk.whitecrescent.waqti.secs
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.Timer

@DisplayName("Timer Tests")
class Timer : BaseTaskTest() {

    @DisplayName("Timer Test Start and Pause")
    @Test
    fun testTimerStartAndPause() {
        val timer = Timer()

        timer.start()

        sleep(2)

        timer.pause()

        assertTrue(timer.duration.secs in 1.95..2.05)
        message(2.00, timer.duration.secs)

    }

    @DisplayName("Timer Test Start and Stop")
    @Test
    fun testTimerStartAndStop() {
        val timer = Timer()

        timer.start()

        sleep(2)

        assertTrue(timer.duration.secs in 1.95..2.05)
        message(2.00, timer.duration.secs)

        timer.stop()

        assertEquals(0L, timer.duration.seconds)

    }

    @DisplayName("Timer Test Start and Pause and Start")
    @Test
    fun testTimerStartAndPauseAndStart() {
        val timer = Timer()

        timer.start()

        sleep(2)

        timer.pause()

        sleep(2)

        timer.start()

        sleep(2)

        timer.pause()

        assertTrue(timer.duration.secs in 3.95..4.05)
        message(4.00, timer.duration.secs)

    }

    @DisplayName("Timer Test Start and Stop and Start")
    @Test
    fun testTimerStartAndStopAndStop() {
        val timer = Timer()

        timer.start()

        sleep(2)

        timer.stop()

        sleep(2)

        timer.start()

        sleep(2)

        timer.pause()

        assertTrue(timer.duration.secs in 1.95..2.05)
        message(2.00, timer.duration.secs)

    }

    @DisplayName("Timer Test Stop with Pause")
    @Test
    fun testTimerStopWithPause() {
        val timer = Timer()

        assertEquals(0L, timer.duration.seconds)

        timer.stop()

        sleep(2)

        assertEquals(0L, timer.duration.seconds)

        timer.pause()

        sleep(2)

        assertEquals(0L, timer.duration.seconds)

    }

    @DisplayName("Timer Pause")
    @Test
    fun testTimerPause() {
        val timer = Timer()

        assertEquals(0L, timer.duration.seconds)

        timer.pause()

        sleep(2)

        assertEquals(0L, timer.duration.seconds)

    }

    /*@DisplayName("Longer Test")
    @Test
    fun test() {
        val timer = Timer()

        timer.start()
        val start = System.currentTimeMillis()

        sleep(3600)

        val end = System.currentTimeMillis()
        timer.pause()

        assertTrue(timer.duration.secs in 3599.95..3600.05)
        message(3600.00, timer.duration.secs)
        println("System Timer: ${(end - start) / 1000.0}")
    }*/
}