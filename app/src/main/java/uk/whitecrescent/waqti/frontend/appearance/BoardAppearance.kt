package uk.whitecrescent.waqti.frontend.appearance

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.kc.unsplash.models.Photo
import io.objectbox.converter.PropertyConverter

class BoardAppearance {

    @SerializedName("bg")
    var backgroundColor: WaqtiColor = WaqtiColor.DEFAULT

    @SerializedName("c")
    var cardColor: WaqtiColor = WaqtiColor.CARD_DEFAULT

    @SerializedName("b")
    var barColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT

    @SerializedName("p")
    var backgroundPhoto: Photo = DEFAULT_PHOTO

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

val DEFAULT_PHOTO: Photo = Photo()