package uk.whitecrescent.waqti.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.persistence.Caches

// Used for testing ObjectBox and Caches, must be in this package so that ObjectBox recognizes it
@Entity
class TestEntity(
        name: String = "",
        number: Int = 0
) : Cacheable {

    @Id
    override var id = 0L

    var name = name
        set(value) {
            field = value
            update()
        }

    var number = number
        set(value) {
            field = value
            update()
        }

    init {
        // the default case occurs only when ObjectBox creates Objects on its own for whatever
        // reason, we will never create Defaults of any Entity!
        if (notDefault()) {
            update()
        }
    }

    override fun notDefault(): Boolean {
        // if all these are false then this has been constructed using default constructor
        return this.name != "" || this.number != 0 || this.id != 0L
    }

    override fun update() {
        Caches.testEntities.put(this)
    }

    override fun hashCode() = hash(id, name, number)

    override fun equals(other: Any?) =
            other is TestEntity &&
                    other.name == this.name &&
                    other.number == this.number

    override fun toString() =
            "Name: $name Number: $number"

}