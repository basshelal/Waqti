package uk.whitecrescent.waqti.model.task

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.hash
import uk.whitecrescent.waqti.model.persistence.Caches

// TODO: 19-May-18 Templates, PropertyBundles and that whole thing need to be tested and doc'd

@Entity
class Template(@Transient val task: Task) : Cacheable {

    @Convert(converter = PropertyBundleConverter::class, dbType = String::class)
    val propertyBundle = PropertyBundle(task)

    @Id
    override var id = 0L

    init {
        update()
    }

    override fun notDefault(): Boolean {
        return false
    }

    override fun update() = Caches.templates.put(this)

    override fun hashCode() = hash(task, propertyBundle)

    override fun equals(other: Any?) =
            other is Template &&
                    other.task == this.task &&
                    other.propertyBundle == this.propertyBundle

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

}