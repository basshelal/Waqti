package uk.whitecrescent.waqti.model.task

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.Time

//region Properties

class TimeProperty(
        @SerializedName("timeIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("timeValue")
        override var value: Time = DEFAULT_TIME,
        @SerializedName("timeIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("timeIsMet")
        override var isMet: Boolean = false) : Property<Time>(isVisible, value, isConstrained, isMet)

class DurationProperty(
        @SerializedName("durationIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("durationValue")
        override var value: Duration = DEFAULT_DURATION,
        @SerializedName("durationIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("durationIsMet")
        override var isMet: Boolean = false) : Property<Duration>(isVisible, value, isConstrained, isMet)

class PriorityProperty(
        @SerializedName("priorityIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("priorityValue")
        override var value: Priority = DEFAULT_PRIORITY,
        @SerializedName("priorityIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("priorityIsMet")
        override var isMet: Boolean = false) : Property<Priority>(isVisible, value, isConstrained, isMet)

class LabelArrayListProperty(
        @SerializedName("labelArrayListIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("labelArrayListValue")
        override var value: ArrayList<Label> = DEFAULT_LABELS_LIST,
        @SerializedName("labelArrayListIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("labelArrayListIsMet")
        override var isMet: Boolean = false) : Property<ArrayList<Label>>(isVisible, value, isConstrained, isMet)

class BooleanProperty(
        @SerializedName("booleanIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("booleanValue")
        override var value: Boolean = false,
        @SerializedName("booleanIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("booleanIsMet")
        override var isMet: Boolean = false) : Property<Boolean>(isVisible, value, isConstrained, isMet)

class StringProperty(
        @SerializedName("stringIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("stringValue")
        override var value: String = "",
        @SerializedName("stringIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("stringIsMet")
        override var isMet: Boolean = false) : Property<String>(isVisible, value, isConstrained, isMet)

class ChecklistProperty(
        @SerializedName("checklistIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("checklistValue")
        override var value: Checklist = DEFAULT_CHECKLIST,
        @SerializedName("checklistIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("checklistIsMet")
        override var isMet: Boolean = false) : Property<Checklist>(isVisible, value, isConstrained, isMet)

class LongProperty(
        @SerializedName("longIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("longValue")
        override var value: Long = 0L,
        @SerializedName("longIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("longIsMet")
        override var isMet: Boolean = false) : Property<Long>(isVisible, value, isConstrained, isMet)

class LongArrayListProperty(
        @SerializedName("longArrayListIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("longArrayListValue")
        override var value: ArrayList<Long> = arrayListOf<Long>(),
        @SerializedName("longArrayListIsConstrained")
        override var isConstrained: Boolean = false,
        @SerializedName("longArrayListIsMet")
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
    val gson = Gson()

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