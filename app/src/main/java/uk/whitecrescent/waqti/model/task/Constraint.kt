package uk.whitecrescent.waqti.model.task

class Constraint<V>(override var isVisible: Boolean,
                    override val value: V,
                    var isMet: Boolean)
    : Property<V>(isVisible, value) {

    companion object {
        fun <T> toProperty(constraint: Constraint<T>) = Property(constraint.isVisible, constraint.value)
    }

    fun toProperty() = toProperty(this)

    val property: Property<V>
        get() = toProperty(this)

    override fun hashCode() =
            value!!.hashCode() + isVisible.hashCode() + isMet.hashCode()

    override fun equals(other: Any?) =
            other is Constraint<*> &&
                    this.value == other.value &&
                    this.isVisible == other.isVisible &&
                    this.isMet == other.isMet

    override fun toString() =
            "isVisible = $isVisible value = ${value.toString()} isMet = $isMet"

    operator fun component3() = isMet

}