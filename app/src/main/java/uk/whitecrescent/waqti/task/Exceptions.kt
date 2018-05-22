package uk.whitecrescent.waqti.task

open class TaskException(string: String) : IllegalStateException(string)

class TaskStateException(string: String, state: TaskState) : TaskException("$string\n State: $state")

class ObserverException(string: String) : TaskException(string)