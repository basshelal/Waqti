package uk.whitecrescent.waqti.backend.task

import uk.whitecrescent.waqti.extensions.hash

//isMet doesn't mean anything if isConstrained is false!
open class Property<V>(@Transient open var isVisible: Boolean = SHOWING,
                       @Transient open var value: V,
                       @Transient open var isConstrained: Boolean = NOT_CONSTRAINED,
                       @Transient open var isMet: Boolean = UNMET) {

    fun constrain(): Property<V> {
        this.isConstrained = true
        return this
    }

    fun unConstrain(): Property<V> {
        this.isConstrained = false
        return this
    }

    operator fun component1() = isVisible

    operator fun component2() = value

    operator fun component3() = isConstrained

    operator fun component4() = isMet

    override fun hashCode() =
            hash(value!!, isVisible, isConstrained, isMet)

    override fun equals(other: Any?) =
            other is Property<*> &&
                    this.value == other.value &&
                    this.isVisible == other.isVisible &&
                    this.isConstrained == other.isConstrained &&
                    this.isMet == other.isMet

    override fun toString() =
            "isVisible = $isVisible value = $value isConstrained = $isConstrained isMet = $isMet"
}