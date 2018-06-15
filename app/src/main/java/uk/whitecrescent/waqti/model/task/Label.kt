package uk.whitecrescent.waqti.model.task

import uk.whitecrescent.waqti.model.Cacheable

class Label
private constructor(var name: String)
    : Cacheable {

    var children = arrayListOf<Label>()

    companion object {

        val allLabels = ArrayList<Label>()

        fun getOrCreateLabel(name: String): Label {
            val newLabel = Label(name)
            val found = allLabels.find { it == newLabel }

            if (found == null) {
                allLabels.add(newLabel)
                return newLabel
            } else return found
        }

        fun getLabel(name: String): Label {
            val newLabel = Label(name)
            val found = allLabels.find { it == newLabel }

            if (found == null) {
                throw IllegalArgumentException("Label not found")
            } else return found
        }

        fun deleteLabel(name: String) {
            for (child in getLabel(name).children) {
                allLabels.remove(child)
            }
            allLabels.remove(getLabel(name))
        }

    }

    override fun id(): ID {
        return System.currentTimeMillis()
    }

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?) =
            other is Label && other.name == this.name

    override fun toString(): String {
        val s = StringBuilder(name)
        if (children.isNotEmpty()) {
            s.append("\n\t$children\n")
        }
        return s.toString()
    }
}