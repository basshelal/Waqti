package uk.whitecrescent.waqti.frontend.appearance

import com.google.gson.Gson
import com.kc.unsplash.models.Photo
import io.objectbox.converter.PropertyConverter

class BoardAppearance {

    var backgroundColor: WaqtiColor = WaqtiColor.DEFAULT

    var cardColor: WaqtiColor = WaqtiColor.CARD_DEFAULT

    var barColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT

    var backgroundPhoto: Photo = defaultPhoto

    companion object {
        val DEFAULT = BoardAppearance()
    }
}

class BoardAppearanceConverter : PropertyConverter<BoardAppearance, String> {

    private val gson = Gson()

    override fun convertToDatabaseValue(entityProperty: BoardAppearance): String {
        return gson.toJson(entityProperty)
    }

    override fun convertToEntityProperty(databaseValue: String): BoardAppearance {
        return gson.fromJson(databaseValue, BoardAppearance::class.java)
    }
}

val defaultPhoto: Photo = Photo()