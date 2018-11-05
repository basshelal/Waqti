package uk.whitecrescent.waqti.model.task

import com.google.gson.annotations.SerializedName
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Time

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
        override var isMet: Boolean = false) :
        Property<ArrayList<Label>>(isVisible, value, isConstrained, isMet)

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
        override var isMet: Boolean = false) :
        Property<ArrayList<Long>>(isVisible, value, isConstrained, isMet)