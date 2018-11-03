package uk.whitecrescent.waqti.model.task

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.hash
import uk.whitecrescent.waqti.model.persistence.Caches

// we've removed the nested labels feature for now as it may prove to be problematic
@Entity
class Label(name: String = "") : Cacheable {

    @Id
    override var id = 0L

    init {
        update()
    }

    companion object {
        fun fromString(string: String) = Label(string)
    }

    //private val _children = hashSetOf<Label>()

    var name = name
        set(value) {
            field = value
            update()
        }

//    val children: List<Label>
//        get() = _children.toList()
//
//    fun addChild(label: Label) {
//        _children.add(label)
//    }
//
//    fun removeChild(label: Label) {
//        _children.remove(label)
//    }

    override fun update() = Caches.labels.put(this)

    //operator fun component1() = name

    //operator fun component2() = children

    override fun hashCode() = hash(name /*children*/)

    override fun equals(other: Any?) =
            other is Label &&
                    other.name == this.name
    //&& other.children == this.children

    override fun toString(): String {
        val s = StringBuilder(name)
//        if (_children.isNotEmpty()) {
//            s.append("\n\t$_children\n")
//        }
        return s.toString()
    }
}