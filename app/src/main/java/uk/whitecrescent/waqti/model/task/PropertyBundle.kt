package uk.whitecrescent.waqti.model.task

import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Time

@Suppress("UNCHECKED_CAST")
class PropertyBundle(val task: Task) {

    private val properties = arrayOf(*ALL_PROPERTIES)

    var time: Property<Time>
        set(value) {
            properties[timeIndex] = value
        }
        get() = properties[timeIndex] as Property<Time>

    var duration: Property<Duration>
        set(value) {
            properties[durationIndex] = value
        }
        get() = properties[durationIndex] as Property<Duration>

    var priority: Property<Priority>
        set(value) {
            properties[priorityIndex] = value
        }
        get() = properties[priorityIndex] as Property<Priority>

    var labels: Property<ArrayList<Label>>
        set(value) {
            properties[labelsIndex] = value
        }
        get() = properties[labelsIndex] as Property<ArrayList<Label>>

    var optional: Property<Optional>
        set(value) {
            properties[optionalIndex] = value
        }
        get() = properties[optionalIndex] as Property<Optional>

    var description: Property<Description>
        set(value) {
            properties[descriptionIndex] = description
        }
        get() = properties[descriptionIndex] as Property<Description>

    var checklist: Property<Checklist>
        set(value) {
            properties[checklistIndex] = value
        }
        get() = properties[checklistIndex] as Property<Checklist>

    var target: Property<Target>
        set(value) {
            properties[targetIndex] = value
        }
        get() = properties[targetIndex] as Property<Target>

    var deadline: Property<Time>
        set(value) {
            properties[deadlineIndex] = value
        }
        get() = properties[deadlineIndex] as Property<Time>

    var before: Property<ID>
        set(value) {
            properties[beforeIndex] = value
        }
        get() = properties[beforeIndex] as Property<ID>

    var subTasks: Property<ArrayList<ID>>
        set(value) {
            properties[subTasksIndex] = value
        }
        get() = properties[subTasksIndex] as Property<ArrayList<ID>>

    init {
        time = task.time
        duration = task.duration
        priority = task.priority
        labels = task.labels
        optional = task.optional
        description = task.description
        checklist = task.checklist
        target = task.target
        deadline = task.deadline
        before = task.before
        subTasks = task.subTasks
        assert(properties.size == 11) // TODO: 19-May-18 remember to remove this
    }

    companion object {
        fun bundlesAreSubset(superBundle: PropertyBundle, subBundle: PropertyBundle): Boolean {
            val list = (0..10).map { true }.toMutableList()
            assert(list.size == 11) // TODO: 22-May-18 remember to remove this

            if (superBundle.time != DEFAULT_TIME_PROPERTY) {
                list[timeIndex] = subBundle.time == superBundle.time
            }
            if (superBundle.duration != DEFAULT_DURATION_PROPERTY) {
                list[durationIndex] = subBundle.duration == superBundle.duration
            }
            if (superBundle.priority != DEFAULT_PRIORITY_PROPERTY) {
                list[priorityIndex] = subBundle.priority == superBundle.priority
            }
            if (superBundle.labels != DEFAULT_LABELS_PROPERTY) {
                list[labelsIndex] = subBundle.labels == superBundle.labels
            }
            if (superBundle.optional != DEFAULT_OPTIONAL_PROPERTY) {
                list[optionalIndex] = subBundle.optional == superBundle.optional
            }
            if (superBundle.description != DEFAULT_DESCRIPTION_PROPERTY) {
                list[descriptionIndex] = subBundle.description == superBundle.description
            }
            if (superBundle.checklist != DEFAULT_CHECKLIST_PROPERTY) {
                list[checklistIndex] = subBundle.checklist == superBundle.checklist
            }
            if (superBundle.deadline != DEFAULT_DEADLINE_PROPERTY) {
                list[deadlineIndex] = subBundle.deadline == superBundle.deadline
            }
            if (superBundle.target != DEFAULT_TARGET_PROPERTY) {
                list[targetIndex] = subBundle.target == superBundle.target
            }
            if (superBundle.before != DEFAULT_BEFORE_PROPERTY) {
                list[beforeIndex] = subBundle.before == superBundle.before
            }
            if (superBundle.subTasks != DEFAULT_SUB_TASKS_PROPERTY) {
                list[subTasksIndex] = subBundle.subTasks == superBundle.subTasks
            }
            return list.all { it == true }
        }
    }

}

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