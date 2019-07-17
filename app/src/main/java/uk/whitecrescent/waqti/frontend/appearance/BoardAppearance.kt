package uk.whitecrescent.waqti.frontend.appearance

import com.google.gson.annotations.SerializedName
import com.kc.unsplash.models.Photo
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.fromJsonTo
import uk.whitecrescent.waqti.toJson

class BoardAppearance {

    @SerializedName("bg")
    var backgroundColor: WaqtiColor = WaqtiColor.BACKGROUND_DEFAULT

    @SerializedName("l")
    var listColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT

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

    override fun convertToDatabaseValue(entityProperty: BoardAppearance?) = entityProperty.toJson

    override fun convertToEntityProperty(databaseValue: String?): BoardAppearance {
        return if (databaseValue == null) {
            BoardAppearance.DEFAULT
        } else databaseValue fromJsonTo BoardAppearance::class.java
    }
}

val DEFAULT_PHOTO: Photo = Photo()