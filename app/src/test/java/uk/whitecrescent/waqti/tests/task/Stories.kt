package uk.whitecrescent.waqti.tests.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.at
import uk.whitecrescent.waqti.model.minutes
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.seconds
import uk.whitecrescent.waqti.model.sleep
import uk.whitecrescent.waqti.model.task.Checklist
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.MANDATORY
import uk.whitecrescent.waqti.model.task.OPTIONAL
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.tasks
import uk.whitecrescent.waqti.model.today
import uk.whitecrescent.waqti.model.tomorrow

// TODO: 27-Mar-18 Finish this too
@DisplayName("Task Stories")
class Stories {

    @DisplayName("Simple Task 1: Go out for a walk")
    @Test
    fun testSimpleTask1() {
        /*
        * "Go out for a walk"
        *     P:
        *         Time: Tomorrow at 16:00
        *         Duration: 30 Minutes
        *         Description: "Walk around the neighborhood to get fresh air while listening to podcast"
        *     C:
        */
        val task = Task("Go out for a walk")
                .setTimePropertyValue(tomorrow at 16)
                .setDurationPropertyValue(30.minutes)
                .setDescriptionValue("Walk around the neighborhood to get fresh air while listening to podcast")

        assertEquals(TaskState.EXISTING, task.state)
        assertTrue(task.getAllShowingConstraints().isEmpty())
        assertEquals(3, task.getAllShowingProperties().size)

        // Simulate the time has come
        task.setTimePropertyValue(now + 1.seconds)
        sleep(2)

        // Task is unchanged since they're all Properties
        assertEquals(TaskState.EXISTING, task.state)
        assertTrue(task.getAllShowingConstraints().isEmpty())
        assertEquals(3, task.getAllShowingProperties().size)

        // Change Duration for testing purposes and start timer
        task.setDurationPropertyValue(2.seconds).startTimer()

        sleep(3)

        assertTrue(task.timerDuration() > task.duration.value)

        // Again Task is unchanged since they're all Properties
        assertEquals(TaskState.EXISTING, task.state)
        assertTrue(task.getAllShowingConstraints().isEmpty())
        assertEquals(3, task.getAllShowingProperties().size)
    }

    @DisplayName("Simple Task 2: Buy groceries")
    @Test
    fun testSimpleTask2() {

        /*
        * "Buy Groceries"
        *     P:
        *         Time: Tomorrow at 11:00
        *         Duration: 60 Minutes
        *         Priority: Low
        *         Labels: Personal, Health
        *         Optional: Yes
        *         Description: "Buy food for this week"
        *         Checklist: * Milk, * Eggs, * Chicken, * Mushrooms
        *         Deadline: Tomorrow at 17:00
        *         Target: Have the ingredient's for this week's food
        *     C:
        */
        val task = Task("Buy Groceries")
                .setTimePropertyValue(tomorrow at 11)
                .setDurationPropertyValue(60.minutes)
                .setPriorityValue(Priority.getOrCreatePriority("Low", 1))
                .setLabelsValue(Label.getOrCreateLabel("Personal"), Label.getOrCreateLabel("Health"))
                .setOptionalValue(OPTIONAL)
                .setDescriptionValue("Buy food for this week")
                .setChecklistPropertyValue(Checklist("Milk", "Eggs", "Chicken", "Mushrooms"))
                .setDeadlinePropertyValue(tomorrow at 17)
                .setTargetPropertyValue("Have the ingredient's for this week's food")

        assertEquals(TaskState.EXISTING, task.state)
        assertTrue(task.getAllShowingConstraints().isEmpty())
        assertEquals(9, task.getAllShowingProperties().size)

    }

    @DisplayName("Story 2")
    @Test
    fun testStory2() {
        val eatTask = Task("Have Breakfast")
                .setTimePropertyValue(today.atTime(9, 0))

        val meditateTask = Task("Meditate after food")
                .setBeforeConstraintValue(eatTask)
                .setTimeConstraintValue(today.atTime(10, 0))
                .setDurationConstraintValue(Duration.ofMinutes(10))
                .setOptionalValue(MANDATORY)

        // simulate eat is done and meditate time is here
        eatTask.kill()
        meditateTask.setTimeConstraintValue(now.minusMinutes(10))

        sleep(2)

        assertEquals(TaskState.EXISTING, meditateTask.state)
        assertThrows(TaskStateException::class.java, { meditateTask.kill() })

        // shorten duration for testing
        meditateTask.setDurationConstraintValue(Duration.ofSeconds(2))

        sleep(2)

        assertThrows(TaskStateException::class.java, { meditateTask.kill() })

        meditateTask.startTimer()

        sleep(4)

        meditateTask.kill()

        assertEquals(TaskState.KILLED, meditateTask.state)
    }

    @DisplayName("Story 3")
    @Test
    fun testStory3() {

        val task = Task("Finish Software Engineering Assignment 1")
                .setDeadlineConstraintValue(today.plusDays(7).atTime(16, 0))
                .addLabels(Label.getOrCreateLabel("University"))
                .setPriorityValue(Priority.getOrCreatePriority("High", 1))
                .addSubTasksConstraint(
                        Task("Write User Requirements")
                                .setDeadlineConstraintValue(today.plusDays(3).atTime(23, 55))
                                .setDescriptionValue("Gather user requirements from surveys and write up document")
                                .setChecklistConstraintValue(Checklist("Gather data", "Write Document")),
                        Task("Write code for requirements")
                                .setDeadlineConstraintValue(today.plusDays(5).atTime(23, 55)),
                        Task("Write effective Unit tests for code")
                                .setDeadlineConstraintValue(today.plusDays(6).atTime(23, 55)),
                        Task("Submit Assignment")
                                .setDeadlineConstraintValue(today.plusDays(7).atTime(12, 0))
                )

        assertThrows(TaskStateException::class.java, { task.kill() })

        sleep(1)

        task.subTasks.value.tasks[0].checklist.value.checkItem(0)
        task.subTasks.value.tasks[0].checklist.value.checkItem(1)

        sleep(1)

        task.subTasks.value.tasks[0].kill()
        task.subTasks.value.tasks[1].kill()
        task.subTasks.value.tasks[2].kill()

        //simulate time has gone past deadline for last subTask
        task.subTasks.value.tasks[3].setDeadlineConstraintValue(now.minusMinutes(30))

        sleep(2)

        assertEquals(TaskState.FAILED, task.subTasks.value.tasks[3].state)
        assertEquals(TaskState.FAILED, task.state)

    }
}