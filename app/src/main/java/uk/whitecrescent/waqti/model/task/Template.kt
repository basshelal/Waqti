package uk.whitecrescent.waqti.model.task

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.hash
import uk.whitecrescent.waqti.model.persistence.Caches

// TODO: 19-May-18 Templates, PropertyBundles and that whole thing need to be tested and doc'd

private const val timeIndex = 0
private const val durationIndex = 1
private const val priorityIndex = 2
private const val labelsIndex = 3
private const val optionalIndex = 4
private const val descriptionIndex = 5
private const val checklistIndex = 6
private const val targetIndex = 7
private const val deadlineIndex = 8
private const val beforeIndex = 9
private const val subTasksIndex = 10

@Entity
class Template(name: String = "", task: Task = Task("")) : Cacheable {

    @Id
    override var id = 0L

    var name = name
        set(value) {
            field = value
            update()
        }

    @Convert(converter = TimePropertyConverter::class, dbType = String::class)
    var time: TimeProperty = task.time

    @Convert(converter = DurationPropertyConverter::class, dbType = String::class)
    var duration: DurationProperty = task.duration

    @Convert(converter = PriorityPropertyConverter::class, dbType = String::class)
    var priority: PriorityProperty = task.priority

    @Convert(converter = LabelArrayListPropertyConverter::class, dbType = String::class)
    var labels: LabelArrayListProperty = task.labels

    @Convert(converter = BooleanPropertyConverter::class, dbType = String::class)
    var optional: BooleanProperty = task.optional

    @Convert(converter = StringPropertyConverter::class, dbType = String::class)
    var description: StringProperty = task.description

    @Convert(converter = ChecklistPropertyConverter::class, dbType = String::class)
    var checklist: ChecklistProperty = task.checklist

    @Convert(converter = StringPropertyConverter::class, dbType = String::class)
    var target: StringProperty = task.target

    @Convert(converter = TimePropertyConverter::class, dbType = String::class)
    var deadline: TimeProperty = task.deadline

    @Convert(converter = LongPropertyConverter::class, dbType = String::class)
    var before: LongProperty = task.before

    @Convert(converter = LongArrayListPropertyConverter::class, dbType = String::class)
    var subTasks: LongArrayListProperty = task.subTasks

    init {
        update()
    }

    override fun notDefault(): Boolean {
        return this.name != "" || this.id != 0L
    }

    override fun update() = Caches.templates.put(this)

    override fun hashCode() = hash(
            time, duration, priority, labels, optional, description, checklist, target,
            deadline, before, subTasks
    )

    override fun equals(other: Any?) =
            other is Template &&
                    other.time == this.time &&
                    other.duration == this.duration &&
                    other.priority == this.priority &&
                    other.labels == this.labels &&
                    other.optional == this.optional &&
                    other.description == this.description &&
                    other.checklist == this.checklist &&
                    other.target == this.target &&
                    other.deadline == this.deadline &&
                    other.before == this.before &&
                    other.subTasks == this.subTasks

    override fun toString(): String {
        return "ID: $id" +
                "Time: $time" +
                " Duration: $duration" +
                " Priority: $priority" +
                " Labels: $labels" +
                " Optional: $optional" +
                " Description: $description" +
                " Checklist: $checklist" +
                " Target: $target" +
                " Deadline: $deadline" +
                " Before: $before" +
                " SubTasks: $subTasks"
    }

    companion object {

        fun templatesAreSubset(superTemplate: Template, subTemplate: Template): Boolean {
            val list = (0..10).map { true }.toMutableList()
            assert(list.size == 11)

            if (superTemplate.time != DEFAULT_TIME_PROPERTY) {
                list[timeIndex] = subTemplate.time == superTemplate.time
            }
            if (superTemplate.duration != DEFAULT_DURATION_PROPERTY) {
                list[durationIndex] = subTemplate.duration == superTemplate.duration
            }
            if (superTemplate.priority != DEFAULT_PRIORITY_PROPERTY) {
                list[priorityIndex] = subTemplate.priority == superTemplate.priority
            }
            if (superTemplate.labels != DEFAULT_LABELS_PROPERTY) {
                list[labelsIndex] = subTemplate.labels == superTemplate.labels
            }
            if (superTemplate.optional != DEFAULT_OPTIONAL_PROPERTY) {
                list[optionalIndex] = subTemplate.optional == superTemplate.optional
            }
            if (superTemplate.description != DEFAULT_DESCRIPTION_PROPERTY) {
                list[descriptionIndex] = subTemplate.description == superTemplate.description
            }
            if (superTemplate.checklist != DEFAULT_CHECKLIST_PROPERTY) {
                list[checklistIndex] = subTemplate.checklist == superTemplate.checklist
            }
            if (superTemplate.deadline != DEFAULT_DEADLINE_PROPERTY) {
                list[deadlineIndex] = subTemplate.deadline == superTemplate.deadline
            }
            if (superTemplate.target != DEFAULT_TARGET_PROPERTY) {
                list[targetIndex] = subTemplate.target == superTemplate.target
            }
            if (superTemplate.before != DEFAULT_BEFORE_PROPERTY) {
                list[beforeIndex] = subTemplate.before == superTemplate.before
            }
            if (superTemplate.subTasks != DEFAULT_SUB_TASKS_PROPERTY) {
                list[subTasksIndex] = subTemplate.subTasks == superTemplate.subTasks
            }
            return list.all { it == true }
        }

        fun fromTemplate(template: Template, title: String) =
                Task(title)
                        .setTimeProperty(template.time)
                        .setDurationProperty(template.duration)
                        .setPriorityProperty(template.priority)
                        .setLabelsProperty(template.labels)
                        .setOptionalProperty(template.optional)
                        .setDescriptionProperty(template.description)
                        .setChecklistProperty(template.checklist)
                        .setDeadlineProperty(template.deadline)
                        .setTargetProperty(template.target)
                        .setBeforeProperty(template.before)
                        .setSubTasksProperty(template.subTasks)
    }

}


