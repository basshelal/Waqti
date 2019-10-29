@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.appearance

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uk.whitecrescent.waqti.extensions.logE
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Color.GREY
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Level.FIFTY
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Level.NINE_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Level.SEVEN_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Level.THREE_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Level.TWO_HUNDRED

open class WaqtiColor(val value: String) {
    init {
        require(value.startsWith('#')) { "$value is invalid, Color must start with #" }
        require(value.length == 7 || value.length == 9) {
            "$value is invalid, Color must be either 7 or 9 characters (includes #)"
        }
        require(value.all { it.isHexChar }) {
            "$value is invalid, contains illegal characters," +
                    " allowed characters are 0-9, a-f (upper and lowercase) and #"
        }
    }

    val toColorDrawable: ColorDrawable
        get() = ColorDrawable(Color.parseColor(value))

    val toAndroidColor: Int
        get() {
            try {
                return Color.parseColor(value)
            } catch (e: IllegalArgumentException) {
                logE("Cannot parse color $value")
                throw e
            }
        }

    inline val colorScheme: ColorScheme
        get() = ColorScheme.getColorSchemeForMainColor(this)

    inline fun withTransparency(string: String): WaqtiColor {
        require(string.length == 2) { "$string is invalid, length must be exactly 2" }
        require(string.all { it.isHexChar }) {
            "$value is invalid, contains illegal characters," +
                    " allowed characters are 0-9, a-f (upper and lowercase)"
        }
        var newValue = ""

        if (value.length == 9) {
            newValue = "#$string${value.substring(3)}"
        }
        if (value.length == 7) {
            newValue = "#$string${value.substring(1)}"
        }

        return WaqtiColor(newValue)
    }

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?) = other.toString() == value

    override fun toString() = value

    companion object {
        val INHERIT = WaqtiColor("#00069420")
        val BLACK = WaqtiColor("#000000")
        val WHITE = WaqtiColor("#FFFFFF")
        val TRANSPARENT = WaqtiColor("#00000000")

        val LIGHT_CARD_DEFAULT = ColorScheme.getColorScheme(GREY, THREE_HUNDRED).main
        val LIGHT_BACKGROUND_DEFAULT = ColorScheme.getColorScheme(GREY, FIFTY).main
        val DARK_CARD_DEFAULT = ColorScheme.getColorScheme(GREY, SEVEN_HUNDRED).main
        val DARK_BACKGROUND_DEFAULT = ColorScheme.getColorScheme(GREY, NINE_HUNDRED).main

        val WAQTI_DEFAULT = ColorScheme.WAQTI_DEFAULT.main
        val WAQTI_WHITE = ColorScheme.getColorScheme(GREY, TWO_HUNDRED).main
    }
}

inline fun colorToHex(androidColor: Int) = String.format("#%06X", 0xFFFFFF and androidColor)

inline val String.toColor: WaqtiColor
    get() = WaqtiColor(this)

inline val Int.toColor: WaqtiColor
    get() = WaqtiColor(colorToHex(this))

inline val Char.isHexChar: Boolean
    get() = (isDigit()
            || toLowerCase() == 'a'
            || toLowerCase() == 'b'
            || toLowerCase() == 'c'
            || toLowerCase() == 'd'
            || toLowerCase() == 'e'
            || toLowerCase() == 'f'
            || toLowerCase() == '#')