package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.ids
import uk.whitecrescent.waqti.model.minutes
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.Checklist
import uk.whitecrescent.waqti.model.task.DEFAULT_BEFORE_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_CHECKLIST_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_DESCRIPTION_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_DURATION_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_LABELS_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_OPTIONAL_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_PRIORITY_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_SUB_TASKS_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_TARGET_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.OPTIONAL
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.PropertyBundle
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.time
import uk.whitecrescent.waqti.model.toArrayList

@DisplayName("Template Task Tests")
class Template : BaseTaskTest() {

    @DisplayName("Sending to Template using Object Functions")
    @Test
    fun testTaskSendToTemplateObject() {
        val task = Task("My Task")
                .setTimePropertyValue(time(2018, 5, 5, 5, 5))
                .setTargetConstraintValue("My Target")
                .setDeadlineConstraintValue(time(2018, 6, 6, 6, 6))


        val template = task.toTemplate()

        Caches.templates.put(template)

        val taskFromTemplate = Task.fromTemplate(Caches.templates[template])

        assertNotEquals(task.title, taskFromTemplate.title)
        assertNotEquals(task.id, taskFromTemplate.id)
        assertNotEquals(task, taskFromTemplate)

        assertEquals("New Task", taskFromTemplate.title)
        assertEquals(task.time, taskFromTemplate.time)
        assertEquals(task.target, taskFromTemplate.target)
        assertEquals(task.deadline, taskFromTemplate.deadline)

        assertEquals(DEFAULT_DURATION_PROPERTY, taskFromTemplate.duration)
        assertEquals(DEFAULT_PRIORITY_PROPERTY, taskFromTemplate.priority)
        assertEquals(DEFAULT_LABELS_PROPERTY, taskFromTemplate.labels)
        assertEquals(DEFAULT_OPTIONAL_PROPERTY, taskFromTemplate.optional)
        assertEquals(DEFAULT_DESCRIPTION_PROPERTY, taskFromTemplate.description)
        assertEquals(DEFAULT_CHECKLIST_PROPERTY, taskFromTemplate.checklist)
        assertEquals(DEFAULT_BEFORE_PROPERTY, taskFromTemplate.before)
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, taskFromTemplate.subTasks)
    }

    @DisplayName("Sending to Template using Task Functions")
    @Test
    fun testTaskSendToTemplateTask() {
        val task = Task("My Task")
                .setTimePropertyValue(time(2018, 5, 5, 5, 5))
                .setTargetConstraintValue("My Target")
                .setDeadlineConstraintValue(time(2018, 6, 6, 6, 6))

        val taskFromTemplate = Task.fromTemplate(task.toTemplate())

        assertNotEquals(task.title, taskFromTemplate.title)
        assertNotEquals(task.id, taskFromTemplate.id)
        assertNotEquals(task, taskFromTemplate)

        assertEquals("New Task", taskFromTemplate.title)
        assertEquals(task.time, taskFromTemplate.time)
        assertEquals(task.target, taskFromTemplate.target)
        assertEquals(task.deadline, taskFromTemplate.deadline)

        assertEquals(DEFAULT_DURATION_PROPERTY, taskFromTemplate.duration)
        assertEquals(DEFAULT_PRIORITY_PROPERTY, taskFromTemplate.priority)
        assertEquals(DEFAULT_LABELS_PROPERTY, taskFromTemplate.labels)
        assertEquals(DEFAULT_OPTIONAL_PROPERTY, taskFromTemplate.optional)
        assertEquals(DEFAULT_DESCRIPTION_PROPERTY, taskFromTemplate.description)
        assertEquals(DEFAULT_CHECKLIST_PROPERTY, taskFromTemplate.checklist)
        assertEquals(DEFAULT_BEFORE_PROPERTY, taskFromTemplate.before)
        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, taskFromTemplate.subTasks)
    }

    @DisplayName("Sending to Template Full")
    @Test
    fun testTaskSendToTemplateFull() {
        val task = Task("My Task")
                .setTimePropertyValue(time(2018, 5, 5, 5, 5))
                .setDurationPropertyValue(30.minutes)
                .setPriorityValue(Priority("Priority", 5))
                .setLabelsValue(Label("Label1"), Label("Label2"))
                .setOptionalValue(OPTIONAL)
                .setDescriptionValue("Description")
                .setChecklistPropertyValue(Checklist("ZERO", "ONE", "TWO"))
                .setDeadlinePropertyValue(time(2018, 6, 6, 6, 6))
                .setTargetConstraintValue("My Target")
                .setBeforePropertyValue(Task("Before"))
                .setSubTasksPropertyValue(
                        arrayListOf(Task("SubTask1"), Task("SubTask2")).ids.toArrayList)


        val taskFromTemplate = Task.fromTemplate(task.toTemplate())

        assertNotEquals(task.title, taskFromTemplate.title)
        assertNotEquals(task.id, taskFromTemplate.id)
        assertNotEquals(task, taskFromTemplate)

        assertEquals(task.time, taskFromTemplate.time)
        assertEquals(task.duration, taskFromTemplate.duration)
        assertEquals(task.priority, taskFromTemplate.priority)
        assertEquals(task.labels, taskFromTemplate.labels)
        assertEquals(task.optional, taskFromTemplate.optional)
        assertEquals(task.description, taskFromTemplate.description)
        assertEquals(task.checklist, taskFromTemplate.checklist)
        assertEquals(task.deadline, taskFromTemplate.deadline)
        assertEquals(task.target, taskFromTemplate.target)
        assertEquals(task.before, taskFromTemplate.before)
        assertEquals(task.subTasks, taskFromTemplate.subTasks)
    }

    @DisplayName("Sending to Template Empty")
    @Test
    fun testTaskSendToTemplateEmpty() {
        val task = Task("My Task")

        val taskFromTemplate = Task.fromTemplate(task.toTemplate())

        assertNotEquals(task.title, taskFromTemplate.title)
        assertNotEquals(task.id, taskFromTemplate.id)
        assertNotEquals(task, taskFromTemplate)

        assertTrue(task.time == taskFromTemplate.time && taskFromTemplate.time == DEFAULT_TIME_PROPERTY)
        assertTrue(task.duration == taskFromTemplate.duration && taskFromTemplate.duration == DEFAULT_DURATION_PROPERTY)
        assertTrue(task.priority == taskFromTemplate.priority && taskFromTemplate.priority == DEFAULT_PRIORITY_PROPERTY)
        assertTrue(task.labels == taskFromTemplate.labels && taskFromTemplate.labels == DEFAULT_LABELS_PROPERTY)
        assertTrue(task.optional == taskFromTemplate.optional && taskFromTemplate.optional == DEFAULT_OPTIONAL_PROPERTY)
        assertTrue(task.description == taskFromTemplate.description && taskFromTemplate.description == DEFAULT_DESCRIPTION_PROPERTY)
        assertTrue(task.checklist == taskFromTemplate.checklist && taskFromTemplate.checklist == DEFAULT_CHECKLIST_PROPERTY)
        assertTrue(task.deadline == taskFromTemplate.deadline && taskFromTemplate.deadline == DEFAULT_DEADLINE_PROPERTY)
        assertTrue(task.target == taskFromTemplate.target && taskFromTemplate.target == DEFAULT_TARGET_PROPERTY)
        assertTrue(task.before == taskFromTemplate.before && taskFromTemplate.before == DEFAULT_BEFORE_PROPERTY)
        assertTrue(task.subTasks == taskFromTemplate.subTasks && taskFromTemplate.subTasks == DEFAULT_SUB_TASKS_PROPERTY)
    }

    @DisplayName("Bundles Are Subset")
    @Test
    fun testTaskBundlesAreSubset() {
        val anonTask = Task()
                .setTimePropertyValue(time(2018, 5, 5, 5, 5))
                .setDurationPropertyValue(30.minutes)
                .setLabelsValue(Label("Label1"), Label("Label2"))
                .setDeadlinePropertyValue(time(2018, 6, 6, 6, 6))

        val realTask = Task("My Task")
                .setTimePropertyValue(time(2018, 5, 5, 5, 5))
                .setDurationPropertyValue(30.minutes)
                .setPriorityValue(Priority("Priority", 5))
                .setLabelsValue(Label("Label1"), Label("Label2"))
                .setOptionalValue(OPTIONAL)
                .setDescriptionValue("Description")
                .setChecklistPropertyValue(Checklist("ZERO", "ONE", "TWO"))
                .setDeadlinePropertyValue(time(2018, 6, 6, 6, 6))
                .setTargetConstraintValue("My Target")
                .setBeforePropertyValue(Task("Before"))
                .setSubTasksPropertyValue(
                        arrayListOf(Task("SubTask1"), Task("SubTask2")).ids.toArrayList)


        assertTrue(Task.taskBundlesAreSubset(anonTask, realTask))
        assertTrue(PropertyBundle.bundlesAreSubset(PropertyBundle(anonTask), PropertyBundle(realTask)))

        val fromTemplate = Task.fromTemplate(anonTask.toTemplate())

        assertTrue(Task.taskBundlesAreSubset(anonTask, fromTemplate))
        assertTrue(PropertyBundle.bundlesAreSubset(PropertyBundle(anonTask), PropertyBundle(fromTemplate)))
    }

}