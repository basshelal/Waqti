package uk.whitecrescent.waqti.backend.collections

import com.google.gson.reflect.TypeToken
import io.objectbox.converter.PropertyConverter
import io.reactivex.schedulers.Schedulers
import uk.whitecrescent.waqti.GSON
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.toJson

class IDArrayListConverter : PropertyConverter<ArrayList<ID>, String> {

    override fun convertToDatabaseValue(entityProperty: ArrayList<ID>?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?): ArrayList<ID> {
        return GSON.fromJson(databaseValue, object : TypeToken<ArrayList<ID>>() {}.type)
    }
}

class ElementNotFoundException(element: Any = "") : NoSuchElementException("Element $element not found")

// Threads for Concurrency
val LIST_OBSERVER_THREAD = Schedulers.newThread()

/**
 * Used to indicate that is either not recommended or not necessary (or both) to override the function, property,
 * constructor or class (even though it is allowed). This would usually be because the current implementation should
 * be sufficient for most applications and therefore an override (without calling super) cannot guarantee a desired
 * outcome.
 *
 * The details of why it is not recommended to override and what actions to take when needed to override should be
 * documented clearly.
 *
 * @author Bassam Helal
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class NoOverride