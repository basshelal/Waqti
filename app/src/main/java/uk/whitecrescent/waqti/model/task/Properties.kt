package uk.whitecrescent.waqti.model.task

import com.google.gson.annotations.SerializedName
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Time

open class TimeProperty(
        @SerializedName("timePIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("timePValue")
        override val value: Time = DEFAULT_TIME) : Property<Time>(isVisible, value)

class TimeConstraint(@SerializedName("timeCIsVisible")
                     override var isVisible: Boolean = false,
                     @SerializedName("timeCValue")
                     override val value: Time = DEFAULT_TIME,
                     @SerializedName("timeCValue")
                     override var isMet: Boolean) : Constraint<Time>(isVisible, value, isMet)

class DurationProperty(
        @SerializedName("durationIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("durationValue")
        override val value: Duration = DEFAULT_DURATION) : Property<Duration>(isVisible, value)

class PriorityProperty(
        @SerializedName("priorityIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("priorityValue")
        override val value: Priority = DEFAULT_PRIORITY) : Property<Priority>(isVisible, value)

class LabelArrayListProperty(
        @SerializedName("labelListIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("labelListValue")
        override val value: ArrayList<Label> = DEFAULT_LABELS_LIST)
    : Property<ArrayList<Label>>(isVisible, value)

class BooleanProperty(
        @SerializedName("booleanIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("booleanValue")
        override val value: Boolean = false) : Property<Boolean>(isVisible, value)

class StringProperty(
        @SerializedName("stringIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("stringValue")
        override val value: String = "") : Property<String>(isVisible, value)

class ChecklistProperty(
        @SerializedName("checklistIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("checklistValue")
        override val value: Checklist = DEFAULT_CHECKLIST) : Property<Checklist>(isVisible, value)

class LongProperty(
        @SerializedName("longIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("longValue")
        override val value: Long = -1L) : Property<Long>(isVisible, value)

class LongArrayListProperty(
        @SerializedName("stringIsVisible")
        override var isVisible: Boolean = false,
        @SerializedName("stringValue")
        override val value: ArrayList<Long> = arrayListOf())
    : Property<ArrayList<Long>>(isVisible, value)