package uk.whitecrescent.waqti.model.collections

import io.objectbox.annotation.Entity

@Entity
open class TypedTaskList(name: String = "") : TaskList(name) {
}