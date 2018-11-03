package uk.whitecrescent.waqti.model.task

import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.model.Time

private val gson = Gson()

class TaskStateConverter : PropertyConverter<TaskState, String> {

    override fun convertToDatabaseValue(entityProperty: TaskState?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, TaskState::class.java)
}

class TimeArrayList : ArrayList<Time>()

class TimeArrayListConverter : PropertyConverter<TimeArrayList, String> {
    val gson = Gson()

    override fun convertToDatabaseValue(entityProperty: TimeArrayList?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, TimeArrayList::class.java)
}

class TimeConverter : PropertyConverter<Time, String> {
    override fun convertToDatabaseValue(entityProperty: Time?) =
            gson.toJson(entityProperty)


    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, Time::class.java)

}