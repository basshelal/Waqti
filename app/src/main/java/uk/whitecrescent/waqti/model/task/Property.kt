package uk.whitecrescent.waqti.model.task

import uk.whitecrescent.waqti.model.hash

open class Property<V>(open var isVisible: Boolean, open val value: V) {

    var id = 0L

    companion object {
        fun <T> toConstraint(property: Property<T>) = Constraint(property.isVisible, property.value, false)
    }

    fun toConstraint() = toConstraint(this)

    // sugar for a cast, unsafe if you're not careful!
    // better to ensure  that it is Constraint before using this so
    // if (x is Constraint) x.asConstraint.toProperty()
    val asConstraint: Constraint<V>
        get() = this as Constraint<V>

    override fun hashCode() =
            hash(value!!, isVisible)

    override fun equals(other: Any?) =
            other is Property<*> &&
                    this.value == other.value &&
                    this.isVisible == other.isVisible

    override fun toString() =
            "isVisible = $isVisible value = $value"

    operator fun component1() = isVisible

    operator fun component2() = value

    val isConstraint: Boolean
        get() = this is Constraint

    val isNotConstraint: Boolean
        get() = this !is Constraint
}

enum class IsConstrained(number: Int) {
    NA(-1),
    NOT_CONSTRAINT(0),
    CONSTRAINT(1)
}

enum class IsMet(number: Int) {
    NA(-1),
    UNMET(0),
    MET(1)
}