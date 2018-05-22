//package uk.whitecrescent.waqti.tests.task
//
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertNotEquals
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import uk.whitecrescent.waqti.Duration
//import uk.whitecrescent.waqti.Time
//import uk.whitecrescent.waqti.task.Checklist
//import uk.whitecrescent.waqti.task.DEFAULT_BEFORE_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_CHECKLIST_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_DEADLINE_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_DESCRIPTION_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_DURATION_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_LABELS_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_OPTIONAL_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_PRIORITY_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_SUB_TASKS_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_TARGET_PROPERTY
//import uk.whitecrescent.waqti.task.DEFAULT_TIME_PROPERTY
//import uk.whitecrescent.waqti.task.Label
//import uk.whitecrescent.waqti.task.OPTIONAL
//import uk.whitecrescent.waqti.task.Priority
//import uk.whitecrescent.waqti.task.Task
//import uk.whitecrescent.waqti.taskIDs
//
//@DisplayName("Template Task Tests")
//class Template {
//
//    @DisplayName("Sending to Template using Object Functions")
//    @Test
//    fun testTaskSendToTemplateObject() {
//        val task = Task("My Task")
//                .setTimePropertyValue(Time.of(2018, 5, 5, 5, 5))
//                .setTargetConstraintValue("My Target")
//                .setDeadlineConstraintValue(Time.of(2018, 6, 6, 6, 6))
//
//
//        TEMPLATE_DATABASE["My Task"] = Task.Template.toTemplate(task)
//
//        val taskFromTemplate = Task.Template.fromTemplate(TEMPLATE_DATABASE["My Task"]!!)
//
//        assertNotEquals(task.title, taskFromTemplate.title)
//        assertNotEquals(task.taskID, taskFromTemplate.taskID)
//        assertNotEquals(task, taskFromTemplate)
//
//        assertEquals("New Task", taskFromTemplate.title)
//        assertEquals(task.time, taskFromTemplate.time)
//        assertEquals(task.target, taskFromTemplate.target)
//        assertEquals(task.deadline, taskFromTemplate.deadline)
//
//        assertEquals(DEFAULT_DURATION_PROPERTY, taskFromTemplate.duration)
//        assertEquals(DEFAULT_PRIORITY_PROPERTY, taskFromTemplate.priority)
//        assertEquals(DEFAULT_LABELS_PROPERTY, taskFromTemplate.labels)
//        assertEquals(DEFAULT_OPTIONAL_PROPERTY, taskFromTemplate.optional)
//        assertEquals(DEFAULT_DESCRIPTION_PROPERTY, taskFromTemplate.description)
//        assertEquals(DEFAULT_CHECKLIST_PROPERTY, taskFromTemplate.checklist)
//        assertEquals(DEFAULT_BEFORE_PROPERTY, taskFromTemplate.before)
//        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, taskFromTemplate.subTasks)
//    }
//
//    @DisplayName("Sending to Template using Task Functions")
//    @Test
//    fun testTaskSendToTemplateTask() {
//        val task = Task("My Task")
//                .setTimePropertyValue(Time.of(2018, 5, 5, 5, 5))
//                .setTargetConstraintValue("My Target")
//                .setDeadlineConstraintValue(Time.of(2018, 6, 6, 6, 6))
//
//
//        val taskFromTemplate = Task("New Task").fromTemplate(task)
//
//        assertNotEquals(task.title, taskFromTemplate.title)
//        assertNotEquals(task.taskID, taskFromTemplate.taskID)
//        assertNotEquals(task, taskFromTemplate)
//
//        assertEquals("New Task", taskFromTemplate.title)
//        assertEquals(task.time, taskFromTemplate.time)
//        assertEquals(task.target, taskFromTemplate.target)
//        assertEquals(task.deadline, taskFromTemplate.deadline)
//
//        assertEquals(DEFAULT_DURATION_PROPERTY, taskFromTemplate.duration)
//        assertEquals(DEFAULT_PRIORITY_PROPERTY, taskFromTemplate.priority)
//        assertEquals(DEFAULT_LABELS_PROPERTY, taskFromTemplate.labels)
//        assertEquals(DEFAULT_OPTIONAL_PROPERTY, taskFromTemplate.optional)
//        assertEquals(DEFAULT_DESCRIPTION_PROPERTY, taskFromTemplate.description)
//        assertEquals(DEFAULT_CHECKLIST_PROPERTY, taskFromTemplate.checklist)
//        assertEquals(DEFAULT_BEFORE_PROPERTY, taskFromTemplate.before)
//        assertEquals(DEFAULT_SUB_TASKS_PROPERTY, taskFromTemplate.subTasks)
//    }
//
//    @DisplayName("Sending to Template Full")
//    @Test
//    fun testTaskSendToTemplateFull() {
//        val task = Task("My Task")
//                .setTimePropertyValue(Time.of(2018, 5, 5, 5, 5))
//                .setDurationPropertyValue(Duration.ofMinutes(30))
//                .setPriorityValue(Priority.getOrCreatePriority("Priority", 5))
//                .setLabelsValue(Label.getOrCreateLabel("Label1"), Label.getOrCreateLabel("Label2"))
//                .setOptionalValue(OPTIONAL)
//                .setDescriptionValue("Description")
//                .setChecklistPropertyValue(Checklist("ZERO", "ONE", "TWO"))
//                .setDeadlinePropertyValue(Time.of(2018, 6, 6, 6, 6))
//                .setTargetConstraintValue("My Target")
//                .setBeforePropertyValue(Task("Before"))
//                .setSubTasksPropertyValue(arrayListOf(Task("SubTask1"), Task("SubTask2")).taskIDs())
//
//
//        val taskFromTemplate = Task("From Template").fromTemplate(task)
//
//        assertNotEquals(task.title, taskFromTemplate.title)
//        assertNotEquals(task.taskID, taskFromTemplate.taskID)
//        assertNotEquals(task, taskFromTemplate)
//
//        assertEquals(task.time, taskFromTemplate.time)
//        assertEquals(task.duration, taskFromTemplate.duration)
//        assertEquals(task.priority, taskFromTemplate.priority)
//        assertEquals(task.labels, taskFromTemplate.labels)
//        assertEquals(task.optional, taskFromTemplate.optional)
//        assertEquals(task.description, taskFromTemplate.description)
//        assertEquals(task.checklist, taskFromTemplate.checklist)
//        assertEquals(task.deadline, taskFromTemplate.deadline)
//        assertEquals(task.target, taskFromTemplate.target)
//        assertEquals(task.before, taskFromTemplate.before)
//        assertEquals(task.subTasks, taskFromTemplate.subTasks)
//    }
//
//    @DisplayName("Sending to Template Empty")
//    @Test
//    fun testTaskSendToTemplateEmpty() {
//        val task = Task("My Task")
//
//        val taskFromTemplate = Task("From Template").fromTemplate(task)
//
//        assertNotEquals(task.title, taskFromTemplate.title)
//        assertNotEquals(task.taskID, taskFromTemplate.taskID)
//        assertNotEquals(task, taskFromTemplate)
//
//        assertTrue(task.time == taskFromTemplate.time && taskFromTemplate.time == DEFAULT_TIME_PROPERTY)
//        assertTrue(task.duration == taskFromTemplate.duration && taskFromTemplate.duration == DEFAULT_DURATION_PROPERTY)
//        assertTrue(task.priority == taskFromTemplate.priority && taskFromTemplate.priority == DEFAULT_PRIORITY_PROPERTY)
//        assertTrue(task.labels == taskFromTemplate.labels && taskFromTemplate.labels == DEFAULT_LABELS_PROPERTY)
//        assertTrue(task.optional == taskFromTemplate.optional && taskFromTemplate.optional == DEFAULT_OPTIONAL_PROPERTY)
//        assertTrue(task.description == taskFromTemplate.description && taskFromTemplate.description == DEFAULT_DESCRIPTION_PROPERTY)
//        assertTrue(task.checklist == taskFromTemplate.checklist && taskFromTemplate.checklist == DEFAULT_CHECKLIST_PROPERTY)
//        assertTrue(task.deadline == taskFromTemplate.deadline && taskFromTemplate.deadline == DEFAULT_DEADLINE_PROPERTY)
//        assertTrue(task.target == taskFromTemplate.target && taskFromTemplate.target == DEFAULT_TARGET_PROPERTY)
//        assertTrue(task.before == taskFromTemplate.before && taskFromTemplate.before == DEFAULT_BEFORE_PROPERTY)
//        assertTrue(task.subTasks == taskFromTemplate.subTasks && taskFromTemplate.subTasks == DEFAULT_SUB_TASKS_PROPERTY)
//    }
//
//    @DisplayName("Bundles Are Subset")
//    @Test
//    fun testTaskBundlesAreSubset() {
//        val anonTask = Task()
//                .setTimePropertyValue(Time.of(2018, 5, 5, 5, 5))
//                .setDurationPropertyValue(Duration.ofMinutes(30))
//                .setLabelsValue(Label.getOrCreateLabel("Label1"), Label.getOrCreateLabel("Label2"))
//                .setDeadlinePropertyValue(Time.of(2018, 6, 6, 6, 6))
//
//        val realTask = Task("My Task")
//                .setTimePropertyValue(Time.of(2018, 5, 5, 5, 5))
//                .setDurationPropertyValue(Duration.ofMinutes(30))
//                .setPriorityValue(Priority.getOrCreatePriority("Priority", 5))
//                .setLabelsValue(Label.getOrCreateLabel("Label1"), Label.getOrCreateLabel("Label2"))
//                .setOptionalValue(OPTIONAL)
//                .setDescriptionValue("Description")
//                .setChecklistPropertyValue(Checklist("ZERO", "ONE", "TWO"))
//                .setDeadlinePropertyValue(Time.of(2018, 6, 6, 6, 6))
//                .setTargetConstraintValue("My Target")
//                .setBeforePropertyValue(Task("Before"))
//                .setSubTasksPropertyValue(arrayListOf(Task("SubTask1"), Task("SubTask2")).taskIDs())
//
//
//        assertTrue(Task.Template.taskBundlesAreSubset(anonTask, realTask))
//        assertTrue(Task.Template.bundlesAreSubset(anonTask.bundle(), realTask.bundle()))
//
//        val fromTemplate = Task.Template.fromTemplate(anonTask.toTemplate())
//
//        assertTrue(Task.Template.taskBundlesAreSubset(anonTask, fromTemplate))
//        assertTrue(Task.Template.bundlesAreSubset(anonTask.bundle(), fromTemplate.bundle()))
//    }
//
//}