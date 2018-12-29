package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.now
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.testTask

@DisplayName("Lifecycle Tests")
class Lifecycle : BaseTaskTest() {

    /*
     * The Task Lifecycle is as follows:
     *
     *            --> E --> K
     *               ^ \
     *              /   \
     *             v     v
     *             S <-- F
     *
     * However the only way to go from Sleeping to Existing is through an ending sleep
     * like Time Constraint as there is no Task.exist() function.
     *
     */

    @DisplayName("EXISTING to SLEEPING")
    @Test
    fun testTaskExistingToSleeping() {
        val task = testTask

        assertEquals(TaskState.EXISTING, task.state)

        task.sleep()

        assertEquals(TaskState.SLEEPING, task.state)
    }

    @DisplayName("SLEEPING to EXISTING")
    @Test
    fun testTaskSleepingToExisting() {
        val task = testTask
                .setTimeConstraintValue(Time.from(now.plusSeconds(1)))

        assertEquals(TaskState.SLEEPING, task.state)

        /*
         * There is no task.exist() function so the only way to re-exist is by using
         * an ending sleep like Time Constraint
         */

        sleep(3)

        assertEquals(TaskState.EXISTING, task.state)

    }

    @DisplayName("EXISTING to FAILED")
    @Test
    fun testTaskExistingToFailed() {
        val task = testTask
        task.isFailable = true

        assertEquals(TaskState.EXISTING, task.state)

        task.fail()

        assertEquals(TaskState.FAILED, task.state)
    }

    @DisplayName("FAILED to SLEEPING")
    @Test
    fun testTaskFailedToSleeping() {
        val task = testTask
        task.isFailable = true

        assertEquals(TaskState.EXISTING, task.state)

        task.fail()

        assertEquals(TaskState.FAILED, task.state)

        task.sleep()

        assertEquals(TaskState.SLEEPING, task.state)
    }

    @DisplayName("EXISTING to KILLED")
    @Test
    fun testTaskExistingToKilled() {
        val task = testTask

        assertEquals(TaskState.EXISTING, task.state)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("EXISTING")
    @Test
    fun testTaskExisting() {
        var task = testTask
        assertEquals(TaskState.EXISTING, task.state)

        task.isFailable = true
        task.fail()

        task = testTask
        task.sleep()

        task = testTask
        task.kill()

    }

    @DisplayName("SLEEPING")
    @Test
    fun testTaskSleeping() {
        val task = testTask
        task.sleep()
        assertEquals(TaskState.SLEEPING, task.state)

        assertThrows(TaskStateException::class.java, { task.sleep() })

        assertThrows(TaskStateException::class.java, { task.kill() })

        task.isFailable = true
        assertThrows(TaskStateException::class.java, { task.fail() })

        assertEquals(TaskState.SLEEPING, task.state)
        task.setTimeConstraintValue(Time.from(now.plusSeconds(1)))
        assertEquals(TaskState.SLEEPING, task.state)

        sleep(3)

        assertEquals(TaskState.EXISTING, task.state)
    }

    @DisplayName("FAILED")
    @Test
    fun testTaskFailed() {
        val task = testTask
        task.isFailable = false
        assertThrows(TaskStateException::class.java, { task.fail() })

        task.isFailable = true
        task.fail()
        assertEquals(TaskState.FAILED, task.state)

        assertThrows(TaskStateException::class.java, { task.fail() })

        assertThrows(TaskStateException::class.java, { task.kill() })

        task.sleep()

        assertEquals(TaskState.SLEEPING, task.state)

        // To go to EXISTING we go through SLEEPING
        task.setTimeConstraintValue(Time.from(now.plusSeconds(1)))
        assertEquals(TaskState.SLEEPING, task.state)

        sleep(3)

        assertEquals(TaskState.EXISTING, task.state)

    }

    @DisplayName("KILLED")
    @Test
    fun testTaskKilled() {
        val task = testTask
        task.isKillable = false
        assertThrows(TaskStateException::class.java, { task.kill() })

        task.isKillable = true
        task.kill()
        assertEquals(TaskState.KILLED, task.state)

        assertThrows(TaskStateException::class.java, { task.kill() })

        task.isFailable = true
        assertThrows(TaskStateException::class.java, { task.fail() })

        assertThrows(TaskStateException::class.java, { task.sleep() })

        task.setTimeConstraintValue(Time.from(now.plusSeconds(1)))
        assertEquals(TaskState.KILLED, task.state)
        sleep(3)
        assertEquals(TaskState.KILLED, task.state)
    }

}