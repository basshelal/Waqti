package uk.whitecrescent.waqti.model.task

import io.reactivex.schedulers.Schedulers
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Time
import java.util.concurrent.TimeUnit

//region Type Aliases

// Type Aliases for more readable code
typealias Description = String

typealias Optional = Boolean
typealias Target = String
typealias ID = Long

//endregion Type Aliases

//region Properties & Constraints


// Used for Properties and Constraints
const val HIDDEN = false
const val SHOWING = true

// Used for Constraints
const val MET = true
const val UNMET = false

//endregion Properties & Constraints

// Used for Optionals
const val OPTIONAL = true
const val MANDATORY = false

//region Task Things

// Used for Tasks
const val DEFAULT_FAILABLE = false
const val DEFAULT_KILLABLE = true
val DEFAULT_TASK_STATE = TaskState.EXISTING

//endregion Task Things

//region Default Property Values

val DEFAULT_TIME: Time = Time.MIN
val DEFAULT_DURATION: Duration = Duration.ZERO
val DEFAULT_PRIORITY = Priority("", -1)
val DEFAULT_LABELS_LIST = arrayListOf<Label>()
const val DEFAULT_OPTIONAL = MANDATORY
const val DEFAULT_DESCRIPTION = ""
val DEFAULT_CHECKLIST = Checklist()
const val DEFAULT_TARGET = ""
val DEFAULT_DEADLINE: Time = Time.MAX
const val DEFAULT_TASK_ID: ID = 0L
val DEFAULT_SUB_TASKS = arrayListOf<ID>()

//endregion Default Property Values

//region Default Properties

val DEFAULT_TIME_PROPERTY = Property(HIDDEN, DEFAULT_TIME)
val DEFAULT_DURATION_PROPERTY = Property(HIDDEN, DEFAULT_DURATION)
val DEFAULT_PRIORITY_PROPERTY = Property(HIDDEN, DEFAULT_PRIORITY)
val DEFAULT_LABELS_PROPERTY = Property(HIDDEN, DEFAULT_LABELS_LIST)
val DEFAULT_OPTIONAL_PROPERTY = Property(HIDDEN, DEFAULT_OPTIONAL)
val DEFAULT_DESCRIPTION_PROPERTY = Property(HIDDEN, DEFAULT_DESCRIPTION)
val DEFAULT_CHECKLIST_PROPERTY = Property(HIDDEN, DEFAULT_CHECKLIST)
val DEFAULT_TARGET_PROPERTY = Property(HIDDEN, DEFAULT_TARGET)
val DEFAULT_DEADLINE_PROPERTY = Property(HIDDEN, DEFAULT_DEADLINE)
val DEFAULT_BEFORE_PROPERTY = Property(HIDDEN, DEFAULT_TASK_ID)
val DEFAULT_SUB_TASKS_PROPERTY = Property(HIDDEN, DEFAULT_SUB_TASKS)

//endregion Default Properties

// Used for deadlines
var GRACE_PERIOD: Duration = Duration.ZERO

//region Concurrency

// Threads for Concurrency
val TIME_CONSTRAINT_THREAD = Schedulers.newThread()
val DURATION_CONSTRAINT_THREAD = Schedulers.newThread()
val CHECKLIST_CONSTRAINT_THREAD = Schedulers.newThread()
val DEADLINE_CONSTRAINT_THREAD = Schedulers.newThread()
val BEFORE_CONSTRAINT_THREAD = Schedulers.newThread()
val SUB_TASKS_CONSTRAINT_THREAD = Schedulers.newThread()

// Used for how often will Observers check stuff on the background threads
const val TIME_CHECKING_PERIOD = 100L
val TIME_CHECKING_UNIT = TimeUnit.MILLISECONDS

//endregion Concurrency


val ALL_PROPERTIES = arrayOf(
        DEFAULT_TIME_PROPERTY,
        DEFAULT_DURATION_PROPERTY,
        DEFAULT_PRIORITY_PROPERTY,
        DEFAULT_LABELS_PROPERTY,
        DEFAULT_OPTIONAL_PROPERTY,
        DEFAULT_DESCRIPTION_PROPERTY,
        DEFAULT_CHECKLIST_PROPERTY,
        DEFAULT_TARGET_PROPERTY,
        DEFAULT_DEADLINE_PROPERTY,
        DEFAULT_BEFORE_PROPERTY,
        DEFAULT_SUB_TASKS_PROPERTY
)

val NUMBER_OF_PROPERTIES = ALL_PROPERTIES.size