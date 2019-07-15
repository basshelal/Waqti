@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.appearance

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uk.whitecrescent.waqti.logE

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

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?) = other.toString() == value

    override fun toString() = value

    companion object {
        val BLACK = WaqtiColor("#000000")
        val WHITE = WaqtiColor("#FFFFFF")
        val WAQTI_WHITE = WaqtiColor("#EEEEEE")
        val CARD_DEFAULT = WaqtiColor("#E0E0E0")
        val WAQTI_DEFAULT = WaqtiColor("#880E4F")
        val DEFAULT = WHITE
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