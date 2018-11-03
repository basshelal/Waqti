package uk.whitecrescent.waqti.model.task

import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter

private val gson = Gson()

class TimePropertyConverter : PropertyConverter<TimeProperty, String> {

    override fun convertToDatabaseValue(entityProperty: TimeProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, TimeProperty::class.java)

}

class DurationPropertyConverter : PropertyConverter<DurationProperty, String> {

    override fun convertToDatabaseValue(entityProperty: DurationProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, DurationProperty::class.java)

}

class PriorityPropertyConverter : PropertyConverter<PriorityProperty, String> {

    override fun convertToDatabaseValue(entityProperty: PriorityProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, PriorityProperty::class.java)

}

class LabelArrayListPropertyConverter : PropertyConverter<LabelArrayListProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LabelArrayListProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, LabelArrayListProperty::class.java)

}

class BooleanPropertyConverter : PropertyConverter<BooleanProperty, String> {

    override fun convertToDatabaseValue(entityProperty: BooleanProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, BooleanProperty::class.java)

}

class StringPropertyConverter : PropertyConverter<StringProperty, String> {

    override fun convertToDatabaseValue(entityProperty: StringProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, StringProperty::class.java)

}

class ChecklistPropertyConverter : PropertyConverter<ChecklistProperty, String> {

    override fun convertToDatabaseValue(entityProperty: ChecklistProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, ChecklistProperty::class.java)

}

class LongPropertyConverter : PropertyConverter<LongProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LongProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, LongProperty::class.java)

}

class LongArrayListPropertyConverter : PropertyConverter<LongArrayListProperty, String> {

    override fun convertToDatabaseValue(entityProperty: LongArrayListProperty?) =
            gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?) =
            gson.fromJson(databaseValue, LongArrayListProperty::class.java)

}