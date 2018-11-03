package uk.whitecrescent.waqti.model.task

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import io.reactivex.disposables.CompositeDisposable
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.Listable

@Entity
class TestTask(title: String = "") : Listable, Cacheable {

    var title = title
        set(value) {
            field = value
            update()
        }

    @Id
    override var id = 0L

    @Convert(converter = TaskStateConverter::class, dbType = String::class)
    var state = DEFAULT_TASK_STATE
        private set

    var isFailable = DEFAULT_FAILABLE

    var isKillable = DEFAULT_KILLABLE

    var age = 0
        private set

    @Convert(converter = TimeArrayListConverter::class, dbType = String::class)
    val failedTimes = TimeArrayList()

    @Convert(converter = TimeConverter::class, dbType = String::class)
    var killedTime = DEFAULT_TIME
        private set

    @Transient
    private val timer = Timer()

    @Transient
    private val composite = CompositeDisposable()

    private fun equalityBundle(): HashMap<String, Any> {
        val bundle = HashMap<String, Any>(8)
        bundle["title"] = this.title
        bundle["state"] = this.state
        bundle["isFailable"] = this.isFailable
        bundle["isKillable"] = this.isKillable
        bundle["age"] = this.age
        bundle["failedTimes"] = this.failedTimes
        bundle["killedTime"] = this.killedTime
        bundle["properties"] = this.getAllProperties()
        return bundle
    }

    @Convert(converter = TimePropertyConverter::class, dbType = String::class)
    var time: TimeProperty = TimeProperty()
        private set

    @Convert(converter = DurationPropertyConverter::class, dbType = String::class)
    var duration: DurationProperty = DurationProperty()
        private set

    @Convert(converter = PriorityPropertyConverter::class, dbType = String::class)
    var priority: PriorityProperty = PriorityProperty()
        private set

    @Convert(converter = LabelArrayListPropertyConverter::class, dbType = String::class)
    var labels: LabelArrayListProperty = LabelArrayListProperty()
        private set

    @Convert(converter = BooleanPropertyConverter::class, dbType = String::class)
    var optional: BooleanProperty = BooleanProperty()
        private set

    @Convert(converter = StringPropertyConverter::class, dbType = String::class)
    var description: StringProperty = StringProperty()
        private set

    @Convert(converter = ChecklistPropertyConverter::class, dbType = String::class)
    var checklist: ChecklistProperty = ChecklistProperty()
        private set

    @Convert(converter = TimePropertyConverter::class, dbType = String::class)
    var deadline: TimeProperty = TimeProperty()
        private set

    @Convert(converter = StringPropertyConverter::class, dbType = String::class)
    var target: StringProperty = StringProperty()
        private set

    @Convert(converter = LongPropertyConverter::class, dbType = String::class)
    var before: LongProperty = LongProperty()
        private set

    @Convert(converter = LongArrayListPropertyConverter::class, dbType = String::class)
    var subTasks: LongArrayListProperty = LongArrayListProperty()
        private set

    private fun makeFailableIfConstraint(property: Property<*>) {
        if (!this.isFailable && property is Constraint) {
            this.isFailable = true
        }
    }

    private fun makeNonFailableIfNoConstraints() {
        if (this.getAllConstraints().isEmpty()) this.isFailable = false
    }

    private fun getAllProperties() = listOf(
            time,
            duration,
            priority,
            labels,
            optional,
            description,
            checklist,
            deadline,
            target,
            before,
            subTasks
    )

//    fun unConstrainAll(): TestTask {
//        if (time.isConstraint) time = time.asConstraint.toProperty()
//        if (duration.isConstraint) duration = duration.asConstraint.toProperty()
//        if (priority.isConstraint) priority = priority.asConstraint.toProperty()
//        if (labels.isConstraint) labels = labels.asConstraint.toProperty()
//        if (optional.isConstraint) optional = optional.asConstraint.toProperty()
//        if (description.isConstraint) description = description.asConstraint.toProperty()
//        if (checklist.isConstraint) checklist = checklist.asConstraint.toProperty()
//        if (deadline.isConstraint) deadline = deadline.asConstraint.toProperty()
//        if (target.isConstraint) target = target.asConstraint.toProperty()
//        if (before.isConstraint) before = before.asConstraint.toProperty()
//        if (subTasks.isConstraint) subTasks = subTasks.asConstraint.toProperty()
//        return this
//    }

    private fun getAllConstraints() =
            getAllProperties().filter { it is Constraint }

    fun getAllShowingProperties() =
            getAllProperties().filter { it.isVisible }

    fun getAllShowingConstraints() =
            getAllConstraints().filter { it.isVisible }

    fun getAllUnmetAndShowingConstraints() =
            getAllConstraints().filter { !(it as Constraint).isMet && it.isVisible }

    override fun hashCode() = equalityBundle().hashCode()

    override fun equals(other: Any?) =
            other is TestTask && other.equalityBundle() == this.equalityBundle()

    override fun toString(): String {
        val result = StringBuilder("$title\n")
        result.append("ID: $id isKillable: $isKillable isFailable: $isFailable state: $state\n")

        result.append("\tP:\n")
        getAllShowingProperties().filter { it !is Constraint }.forEach { result.append("\t\t$it\n") }

        result.append("\tC:\n")
        getAllShowingConstraints().forEach { result.append("\t\t$it\n") }

        return result.toString()
    }

    override fun update() {

    }
}