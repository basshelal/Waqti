package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.at
import uk.whitecrescent.waqti.hours
import uk.whitecrescent.waqti.ids
import uk.whitecrescent.waqti.minutes
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
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.now
import uk.whitecrescent.waqti.tomorrow

@DisplayName("Template Task Tests")
class Template : BaseTaskTest() {

    @DisplayName("Sending to Template using Object Functions")
    @Test
    fun testTaskSendToTemplateObject() {
        val task = Task("My Task")
                .setTimePropertyValue(now + 6.hours)
                .setTargetConstraintValue("My Target")
                .setDeadlineConstraintValue(tomorrow at 11)


        val template = task.toTemplate()

        Caches.templates.put(template)

        val taskFromTemplate = Task.fromTemplate(Caches.templates[template], "From Template")

        assertNotEquals(task.name, taskFromTemplate.name)
        assertNotEquals(task.id, taskFromTemplate.id)
        assertNotEquals(task, taskFromTemplate)

        assertEquals("From Template", taskFromTemplate.name)
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
                .setTimePropertyValue(now + 6.hours)
                .setTargetConstraintValue("My Target")
                .setDeadlineConstraintValue(tomorrow at 11)

        val taskFromTemplate = Task.fromTemplate(task.toTemplate(), "From Template")

        assertNotEquals(task.name, taskFromTemplate.name)
        assertNotEquals(task.id, taskFromTemplate.id)
        assertNotEquals(task, taskFromTemplate)

        assertEquals("From Template", taskFromTemplate.name)
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
                .setTimePropertyValue(now + 6.hours)
                .setDurationPropertyValue(30.minutes)
                .setPriorityValue(Priority("Priority", 5))
                .setLabelsValue(Label("Label1"), Label("Label2"))
                .setOptionalValue(OPTIONAL)
                .setDescriptionValue("Description")
                .setChecklistPropertyValue(Checklist("ZERO", "ONE", "TWO"))
                .setDeadlinePropertyValue(tomorrow at 11)
                .setTargetConstraintValue("My Target")
                .setBeforePropertyValue(Task("Before"))
                .setSubTasksPropertyValue(
                        arrayListOf(Task("SubTask1"), Task("SubTask2")).ids)


        val taskFromTemplate = Task.fromTemplate(task.toTemplate(), "From Template")

        assertNotEquals(task.name, taskFromTemplate.name)
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

        val taskFromTemplate = Task.fromTemplate(task.toTemplate(), "From Template")

        assertNotEquals(task.name, taskFromTemplate.name)
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
        val time = now + 6.hours
        val deadline = tomorrow at 11
        val labels = listOf(Label("Label1"), Label("Label2")).toTypedArray()

        val anonTask = Task()
                .setTimePropertyValue(time)
                .setDurationPropertyValue(30.minutes)
                .setLabelsValue(*labels)
                .setDeadlinePropertyValue(deadline)

        val realTask = Task("My Task")
                .setTimePropertyValue(time)
                .setDurationPropertyValue(30.minutes)
                .setPriorityValue(Priority("Priority", 5))
                .setLabelsValue(*labels)
                .setOptionalValue(OPTIONAL)
                .setDescriptionValue("Description")
                .setChecklistPropertyValue(Checklist("ZERO", "ONE", "TWO"))
                .setDeadlinePropertyValue(deadline)
                .setTargetConstraintValue("My Target")
                .setBeforePropertyValue(Task("Before"))
                .setSubTasksPropertyValue(
                        arrayListOf(Task("SubTask1"), Task("SubTask2")).ids)

        assertTrue(Task.taskTemplatesAreSubset(anonTask, realTask))
        assertTrue(Template.templatesAreSubset(anonTask.toTemplate(), realTask.toTemplate()))

        val fromTemplate = Task.fromTemplate(anonTask.toTemplate(), "")

        assertTrue(Task.taskTemplatesAreSubset(anonTask, fromTemplate))
        assertTrue(Template.templatesAreSubset(anonTask.toTemplate(), fromTemplate.toTemplate()))
    }

}