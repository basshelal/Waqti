//package uk.whitecrescent.waqti.model.task
//
//import io.objectbox.annotation.Convert
//import io.objectbox.annotation.Entity
//import uk.whitecrescent.waqti.model.hash
//
//private const val timeIndex = 0
//private const val durationIndex = 1
//private const val priorityIndex = 2
//private const val labelsIndex = 3
//private const val optionalIndex = 4
//private const val descriptionIndex = 5
//private const val checklistIndex = 6
//private const val targetIndex = 7
//private const val deadlineIndex = 8
//private const val beforeIndex = 9
//private const val subTasksIndex = 10
//
//@Entity
////@Suppress("UNCHECKED_CAST")
//class PropertyBundle(@Transient val task: Task) {
//
//    @Convert(converter = TimePropertyConverter::class, dbType = String::class)
//    var time: TimeProperty = task.time
//
//    @Convert(converter = DurationPropertyConverter::class, dbType = String::class)
//    var duration: DurationProperty = task.duration
//
//    @Convert(converter = PriorityPropertyConverter::class, dbType = String::class)
//    var priority: PriorityProperty = task.priority
//
//    @Convert(converter = LabelArrayListPropertyConverter::class, dbType = String::class)
//    var labels: LabelArrayListProperty = task.labels
//
//    @Convert(converter = BooleanPropertyConverter::class, dbType = String::class)
//    var optional: BooleanProperty = task.optional
//
//    @Convert(converter = StringPropertyConverter::class, dbType = String::class)
//    var description: StringProperty = task.description
//
//    @Convert(converter = ChecklistPropertyConverter::class, dbType = String::class)
//    var checklist: ChecklistProperty = task.checklist
//
//    @Convert(converter = StringPropertyConverter::class, dbType = String::class)
//    var target: StringProperty = task.target
//
//    @Convert(converter = TimePropertyConverter::class, dbType = String::class)
//    var deadline: TimeProperty = task.deadline
//
//    @Convert(converter = LongPropertyConverter::class, dbType = String::class)
//    var before: LongProperty = task.before
//
//    @Convert(converter = LongArrayListPropertyConverter::class, dbType = String::class)
//    var subTasks: LongArrayListProperty = task.subTasks
//
//    override fun hashCode(): Int {
//        return hash(
//                time, duration, priority, labels, optional, description, checklist, target,
//                deadline, before, subTasks
//        )
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return other is PropertyBundle &&
//                other.time == this.time &&
//                other.duration == this.duration &&
//                other.priority == this.priority &&
//                other.labels == this.labels &&
//                other.optional == this.optional &&
//                other.description == this.description &&
//                other.checklist == this.checklist &&
//                other.target == this.target &&
//                other.deadline == this.deadline &&
//                other.before == this.before &&
//                other.subTasks == this.subTasks
//
//    }
//
//    override fun toString(): String {
//        return "Time: $time" +
//                " Duration: $duration" +
//                " Priority: $priority" +
//                " Labels: $labels" +
//                " Optional: $optional" +
//                " Description: $description" +
//                " Checklist: $checklist" +
//                " Target: $target" +
//                " Deadline: $deadline" +
//                " Before: $before" +
//                " SubTasks: $subTasks"
//    }
//
//    companion object {
//        fun bundlesAreSubset(superBundle: PropertyBundle, subBundle: PropertyBundle): Boolean {
//            val list = (0..10).map { true }.toMutableList()
//            assert(list.size == 11)
//
//            if (superBundle.time != DEFAULT_TIME_PROPERTY) {
//                list[timeIndex] = subBundle.time == superBundle.time
//            }
//            if (superBundle.duration != DEFAULT_DURATION_PROPERTY) {
//                list[durationIndex] = subBundle.duration == superBundle.duration
//            }
//            if (superBundle.priority != DEFAULT_PRIORITY_PROPERTY) {
//                list[priorityIndex] = subBundle.priority == superBundle.priority
//            }
//            if (superBundle.labels != DEFAULT_LABELS_PROPERTY) {
//                list[labelsIndex] = subBundle.labels == superBundle.labels
//            }
//            if (superBundle.optional != DEFAULT_OPTIONAL_PROPERTY) {
//                list[optionalIndex] = subBundle.optional == superBundle.optional
//            }
//            if (superBundle.description != DEFAULT_DESCRIPTION_PROPERTY) {
//                list[descriptionIndex] = subBundle.description == superBundle.description
//            }
//            if (superBundle.checklist != DEFAULT_CHECKLIST_PROPERTY) {
//                list[checklistIndex] = subBundle.checklist == superBundle.checklist
//            }
//            if (superBundle.deadline != DEFAULT_DEADLINE_PROPERTY) {
//                list[deadlineIndex] = subBundle.deadline == superBundle.deadline
//            }
//            if (superBundle.target != DEFAULT_TARGET_PROPERTY) {
//                list[targetIndex] = subBundle.target == superBundle.target
//            }
//            if (superBundle.before != DEFAULT_BEFORE_PROPERTY) {
//                list[beforeIndex] = subBundle.before == superBundle.before
//            }
//            if (superBundle.subTasks != DEFAULT_SUB_TASKS_PROPERTY) {
//                list[subTasksIndex] = subBundle.subTasks == superBundle.subTasks
//            }
//            return list.all { it == true }
//        }
//    }
//}