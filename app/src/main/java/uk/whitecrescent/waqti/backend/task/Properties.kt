package uk.whitecrescent.waqti.backend.task

import com.google.gson.annotations.SerializedName
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.fromJsonTo
import uk.whitecrescent.waqti.toJson

//region Properties

class TimeProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: Time = DEFAULT_TIME,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<Time>(isVisible, value, isConstrained, isMet)

class DurationProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: Duration = DEFAULT_DURATION,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<Duration>(isVisible, value, isConstrained, isMet)

class PriorityProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: Priority = DEFAULT_PRIORITY,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<Priority>(isVisible, value, isConstrained, isMet)

class LabelArrayListProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: ArrayList<Label> = DEFAULT_LABELS_LIST,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<ArrayList<Label>>(isVisible, value, isConstrained, isMet)

class BooleanProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: Boolean = false,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<Boolean>(isVisible, value, isConstrained, isMet)

class StringProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: String = "",
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<String>(isVisible, value, isConstrained, isMet)

class ChecklistProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: Checklist = DEFAULT_CHECKLIST,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<Checklist>(isVisible, value, isConstrained, isMet)

class LongProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: Long = 0L,
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<Long>(isVisible, value, isConstrained, isMet)

class LongArrayListProperty(
        @SerializedName("V")
        override var isVisible: Boolean = false,
        @SerializedName("v")
        override var value: ArrayList<Long> = arrayListOf(),
        @SerializedName("C")
        override var isConstrained: Boolean = false,
        @SerializedName("M")
        override var isMet: Boolean = false) : Property<ArrayList<Long>>(isVisible, value, isConstrained, isMet)

//endregion Properties

//region Converters

class TimePropertyConverter : PropertyConverter<TimeProperty, String> {

    override fun convertToDatabaseValue(entityProperty: TimeProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo TimeProperty::class.java

}

class DurationPropertyConverter : PropertyConverter<DurationProperty, String> {

    override fun convertToDatabaseValue(entityProperty: DurationProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo DurationProperty::class.java

}

class PriorityPropertyConverter : PropertyConverter<PriorityProperty, String> {

    override fun convertToDatabaseValue(entityProperty: PriorityProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo PriorityProperty::class.java

}

class LabelArrayListPropertyConverter : PropertyConverter<LabelArrayListProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LabelArrayListProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo LabelArrayListProperty::class.java

}

class BooleanPropertyConverter : PropertyConverter<BooleanProperty, String> {

    override fun convertToDatabaseValue(entityProperty: BooleanProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo BooleanProperty::class.java

}

class StringPropertyConverter : PropertyConverter<StringProperty, String> {

    override fun convertToDatabaseValue(entityProperty: StringProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo StringProperty::class.java

}

class ChecklistPropertyConverter : PropertyConverter<ChecklistProperty, String> {

    override fun convertToDatabaseValue(entityProperty: ChecklistProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo ChecklistProperty::class.java

}

class LongPropertyConverter : PropertyConverter<LongProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LongProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo LongProperty::class.java

}

class LongArrayListPropertyConverter : PropertyConverter<LongArrayListProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LongArrayListProperty?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo LongArrayListProperty::class.java

}

class TaskStateConverter : PropertyConverter<TaskState, String> {

    override fun convertToDatabaseValue(entityProperty: TaskState?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo TaskState::class.java
}

class TimeArrayList : ArrayList<Time>()

class TimeArrayListConverter : PropertyConverter<TimeArrayList, String> {

    override fun convertToDatabaseValue(entityProperty: TimeArrayList?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo TimeArrayList::class.java
}

class TimeConverter : PropertyConverter<Time, String> {

    override fun convertToDatabaseValue(entityProperty: Time?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo Time::class.java

}

class DurationConverter : PropertyConverter<Duration, String> {

    override fun convertToDatabaseValue(entityProperty: Duration?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?) = databaseValue fromJsonTo Duration::class.java

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