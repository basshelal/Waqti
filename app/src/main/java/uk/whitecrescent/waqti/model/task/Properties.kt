package uk.whitecrescent.waqti.model.task

import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.Time

//region Properties

class TimeProperty(
        override var isVisible: Boolean = false,
        override var value: Time = DEFAULT_TIME,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<Time>(isVisible, value, isConstrained, isMet)

class DurationProperty(
        override var isVisible: Boolean = false,
        override var value: Duration = DEFAULT_DURATION,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<Duration>(isVisible, value, isConstrained, isMet)

class PriorityProperty(
        override var isVisible: Boolean = false,
        override var value: Priority = DEFAULT_PRIORITY,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<Priority>(isVisible, value, isConstrained, isMet)

class LabelArrayListProperty(
        override var isVisible: Boolean = false,
        override var value: ArrayList<Label> = DEFAULT_LABELS_LIST,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<ArrayList<Label>>(isVisible, value, isConstrained, isMet)

class BooleanProperty(
        override var isVisible: Boolean = false,
        override var value: Boolean = false,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<Boolean>(isVisible, value, isConstrained, isMet)

class StringProperty(
        override var isVisible: Boolean = false,
        override var value: String = "",
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<String>(isVisible, value, isConstrained, isMet)

class ChecklistProperty(
        override var isVisible: Boolean = false,
        override var value: Checklist = DEFAULT_CHECKLIST,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<Checklist>(isVisible, value, isConstrained, isMet)

class LongProperty(
        override var isVisible: Boolean = false,
        override var value: Long = 0L,
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<Long>(isVisible, value, isConstrained, isMet)

class LongArrayListProperty(
        override var isVisible: Boolean = false,
        override var value: ArrayList<Long> = arrayListOf(),
        override var isConstrained: Boolean = false,
        override var isMet: Boolean = false) : Property<ArrayList<Long>>(isVisible, value, isConstrained, isMet)

//endregion Properties

//region Converters

private val gson = Gson()

class TimePropertyConverter : PropertyConverter<TimeProperty, String> {

    override fun convertToDatabaseValue(entityProperty: TimeProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, TimeProperty::class.java)

}

class DurationPropertyConverter : PropertyConverter<DurationProperty, String> {

    override fun convertToDatabaseValue(entityProperty: DurationProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, DurationProperty::class.java)

}

class PriorityPropertyConverter : PropertyConverter<PriorityProperty, String> {

    override fun convertToDatabaseValue(entityProperty: PriorityProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, PriorityProperty::class.java)

}

class LabelArrayListPropertyConverter : PropertyConverter<LabelArrayListProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LabelArrayListProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, LabelArrayListProperty::class.java)

}

class BooleanPropertyConverter : PropertyConverter<BooleanProperty, String> {

    override fun convertToDatabaseValue(entityProperty: BooleanProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, BooleanProperty::class.java)

}

class StringPropertyConverter : PropertyConverter<StringProperty, String> {

    override fun convertToDatabaseValue(entityProperty: StringProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, StringProperty::class.java)

}

class ChecklistPropertyConverter : PropertyConverter<ChecklistProperty, String> {

    override fun convertToDatabaseValue(entityProperty: ChecklistProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, ChecklistProperty::class.java)

}

class LongPropertyConverter : PropertyConverter<LongProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LongProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, LongProperty::class.java)

}

class LongArrayListPropertyConverter : PropertyConverter<LongArrayListProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LongArrayListProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, LongArrayListProperty::class.java)

}

class TaskStateConverter : PropertyConverter<TaskState, String> {

    override fun convertToDatabaseValue(entityProperty: TaskState?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, TaskState::class.java)
}

class TimeArrayList : ArrayList<Time>()

class TimeArrayListConverter : PropertyConverter<TimeArrayList, String> {

    override fun convertToDatabaseValue(entityProperty: TimeArrayList?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, TimeArrayList::class.java)
}

class TimeConverter : PropertyConverter<Time, String> {
    override fun convertToDatabaseValue(entityProperty: Time?) =
            gson.toJson(entityProperty)


    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, Time::class.java)

}

class DurationConverter : PropertyConverter<Duration, String> {
    override fun convertToDatabaseValue(entityProperty: Duration?) =
            gson.toJson(entityProperty)


    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, Duration::class.java)

}

//endregion Converters

enum class Properties {
    TIME,
    DURATION,
    PRIORITY,
    LABELS,
    OPTIONAL,
    DESCRIPTION,
    CHECKLIST,
    TARGET,
    DEADLINE,
    BEFORE,
    SUB_TASKS
}

val NUMBER_OF_PROPERTIES = Properties.values().size