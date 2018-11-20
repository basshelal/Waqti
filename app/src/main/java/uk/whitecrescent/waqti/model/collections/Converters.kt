package uk.whitecrescent.waqti.model.collections

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.model.task.ID

private val gson = Gson()

class IDArrayListConverter : PropertyConverter<ArrayList<ID>, String> {

    override fun convertToDatabaseValue(entityProperty: ArrayList<ID>?): String {
        return gson.toJson(entityProperty)
    }

    override fun convertToEntityProperty(databaseValue: String?): ArrayList<ID> {
        return gson.fromJson(databaseValue, object : TypeToken<ArrayList<ID>>() {}.type)
    }
}
