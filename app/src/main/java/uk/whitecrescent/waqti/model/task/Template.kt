package uk.whitecrescent.waqti.task

import uk.whitecrescent.waqti.model.Cache
import uk.whitecrescent.waqti.model.Cacheable

// TODO: 19-May-18 Templates, PropertyBundles and that whole thing need to be tested and doc'd

class Template(val task: Task) : Cacheable {

    private val propertyBundle = PropertyBundle(task)
    private val id = Cache.newTemplateID()

    init {
        Cache.putTemplate(this)
    }

    companion object {

        fun fromTemplate(template: Template, title: String = "New Task"): Task {
            val task = Task(title)
            val propertyBundle = template.propertyBundle
            task.setTimeProperty(propertyBundle.time)
            task.setDurationProperty(propertyBundle.duration)
            task.setPriorityProperty(propertyBundle.priority)
            task.setLabelsProperty(propertyBundle.labels)
            task.setOptionalProperty(propertyBundle.optional)
            task.setDescriptionProperty(propertyBundle.description)
            task.setChecklistProperty(propertyBundle.checklist)
            task.setDeadlineProperty(propertyBundle.deadline)
            task.setTargetProperty(propertyBundle.target)
            task.setBeforeProperty(propertyBundle.before)
            task.setSubTasksProperty(propertyBundle.subTasks)
            return task
        }

    }

    override fun id() = id
}