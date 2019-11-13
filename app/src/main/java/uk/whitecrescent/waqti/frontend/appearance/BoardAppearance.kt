package uk.whitecrescent.waqti.frontend.appearance

import com.github.basshelal.unsplashpicker.data.UnsplashLinks
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.github.basshelal.unsplashpicker.data.UnsplashUrls
import com.github.basshelal.unsplashpicker.data.UnsplashUser
import com.google.gson.annotations.SerializedName
import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.extensions.fromJsonTo
import uk.whitecrescent.waqti.extensions.toJson

class BoardAppearance {

    @SerializedName("b")
    var barColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT

    @SerializedName("l")
    var listColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT

    @SerializedName("c")
    var cardColor: WaqtiColor = WaqtiColor.LIGHT_CARD_DEFAULT

    @SerializedName("bg")
    var backgroundColor: WaqtiColor = WaqtiColor.LIGHT_BACKGROUND_DEFAULT

    @SerializedName("p")
    var backgroundPhoto: UnsplashPhoto = DEFAULT_PHOTO

    @SerializedName("t")
    var backgroundType: BackgroundType = BackgroundType.COLOR

    companion object {
        val DEFAULT_DARK = BoardAppearance().apply {
            backgroundColor = WaqtiColor.DARK_BACKGROUND_DEFAULT
            cardColor = WaqtiColor.DARK_CARD_DEFAULT
        }
        val DEFAULT_LIGHT = BoardAppearance()
        val DEFAULT = DEFAULT_LIGHT
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

enum class BackgroundType {
    COLOR, UNSPLASH_PHOTO
}

val DEFAULT_URLS = UnsplashUrls("", "", "", "", "")
val DEFAULT_LINKS = UnsplashLinks("", "", "", "", "", "", "")
val DEFAULT_USER = UnsplashUser("", "", "", "", "", "", 0, 0, 0, DEFAULT_URLS, DEFAULT_LINKS)

val DEFAULT_PHOTO = UnsplashPhoto("", "", 0, 0, "", 0, "",
        DEFAULT_URLS, DEFAULT_LINKS, DEFAULT_USER, null)