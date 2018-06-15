package uk.whitecrescent.waqti.model.task

import uk.whitecrescent.waqti.model.Cacheable

/**
 * The user defined level of importance of a Task represented as a String with a number representing importance
 * level.
 *
 * Priority is particularly useful in solving or mediating Task collisions within collections. A Task collision
 * occurs when two or more Tasks in a collection share the same time, if they have different priority levels then
 * the Task with the higher priority level will be shown and a collision warning will be displayed to the user,
 * this is called a weak collision. If the tasks have equal priority levels then the user must mediate or solve
 * the collision themselves, this is called a strong collision.
 *
 * Priority can not be a Constraint.
 *
 * @see Task
 * @author Bassam Helal
 */
class Priority
private constructor(var name: String, var importanceLevel: Int)
    : Cacheable {

    companion object {

        val allPriorities = ArrayList<Priority>()

        fun getOrCreatePriority(name: String, importanceLevel: Int): Priority {
            val newPriority = Priority(name, importanceLevel)
            val found = allPriorities.find { it == newPriority }

            if (found == null) {
                allPriorities.add(newPriority)
                return newPriority
            } else return found
        }

        fun getPriority(name: String, importanceLevel: Int): Priority {
            val newPriority = Priority(name, importanceLevel)
            val found = allPriorities.find { it == newPriority }

            if (found == null) {
                throw IllegalArgumentException("Priority not found")
            } else return found
        }

        fun deletePriority(name: String, importanceLevel: Int) {
            allPriorities.remove(getPriority(name, importanceLevel))
        }
    }

    override fun id(): ID {
        return System.currentTimeMillis()
    }

    override fun hashCode() = name.hashCode() + importanceLevel.hashCode()

    override fun equals(other: Any?) =
            other is Priority &&
                    other.name == this.name &&
                    other.importanceLevel == this.importanceLevel

    override fun toString() = "$name $importanceLevel"

}