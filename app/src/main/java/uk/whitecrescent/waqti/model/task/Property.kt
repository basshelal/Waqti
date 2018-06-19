package uk.whitecrescent.waqti.model.task

open class Property<V>(open var isVisible: Boolean, open val value: V) {

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
            value!!.hashCode() + isVisible.hashCode()

    override fun equals(other: Any?) =
            other is Property<*> &&
                    this.value == other.value &&
                    this.isVisible == other.isVisible

    override fun toString() =
            "isVisible = $isVisible value = $value"

    operator fun component1() = isVisible

    operator fun component2() = value

    val isNotConstraint: Boolean
        get() = this !is Constraint
}