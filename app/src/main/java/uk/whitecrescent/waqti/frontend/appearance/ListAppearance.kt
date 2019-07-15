package uk.whitecrescent.waqti.frontend.appearance

import com.google.gson.annotations.SerializedName
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.fromJsonTo
import uk.whitecrescent.waqti.toJson

class ListAppearance {

    @SerializedName("bg")
    var headerColor: WaqtiColor = WaqtiColor.DEFAULT

    @SerializedName("c")
    var cardColor: WaqtiColor = WaqtiColor.CARD_DEFAULT

    companion object {
        val DEFAULT = ListAppearance()
    }
}

class ListAppearanceConverter : PropertyConverter<ListAppearance, String> {

    override fun convertToDatabaseValue(entityProperty: ListAppearance?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?): ListAppearance {
        return if (databaseValue == null) {
            ListAppearance.DEFAULT
        } else databaseValue fromJsonTo ListAppearance::class.java
    }
}