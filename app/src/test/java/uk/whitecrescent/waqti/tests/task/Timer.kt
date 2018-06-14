package uk.whitecrescent.waqti.tests.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.task.Timer

@DisplayName("Timer Tests")
class Timer {

    @DisplayName("Timer Test Start and Pause")
    @Test
    fun testTimerStartAndPause() {
        val timer = Timer()

        timer.start()

        sleep(2)

        timer.pause()

        assertTrue(timer.duration.seconds >= 1.999 || timer.duration.seconds <= 2.001)

    }

    @DisplayName("Timer Test Start and Stop")
    @Test
    fun testTimerStartAndStop() {
        val timer = Timer()

        timer.start()

        sleep(2)

        assertTrue(timer.duration.seconds >= 1.999 || timer.duration.seconds <= 2.001)

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

        assertTrue(timer.duration.seconds >= 3.999 || timer.duration.seconds <= 4.001)

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

        assertTrue(timer.duration.seconds >= 1.999 || timer.duration.seconds <= 2.001)

    }

    @DisplayName("Timer Test Stop with Pause")
    @Test
    fun testTimerStopWithPause() {
        val timer = Timer()

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

        timer.pause()

        sleep(2)

        assertEquals(0L, timer.duration.seconds)

    }
}