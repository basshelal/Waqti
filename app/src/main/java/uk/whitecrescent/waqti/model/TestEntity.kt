package uk.whitecrescent.waqti.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.persistence.Caches

// Used for testing ObjectBox and Caches, must be here so that ObjectBox recognizes it
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
        // TODO: 08-Nov-18 this causes problems!
        // since ObjectBox will keep creating new Objects so this will always be called
        // this can be solved by making the ID assignable, we can put the entity in the map first
        // then into the DB, we currently do it the other way around since we rely on ObjectBox
        // to provide us with the ids which require us to have to put it first to get that
        if (notDefault()) {
            update()
        } // the default!
        //update()
    }

    private fun notDefault(): Boolean {
        // if all these are false then this is constructed using default constructor
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