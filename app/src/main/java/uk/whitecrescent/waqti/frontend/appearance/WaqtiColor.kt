package uk.whitecrescent.waqti.frontend.appearance

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

open class WaqtiColor(val value: String) {
    init {
        require(value.startsWith('#')) { "Color must start with #" }
        require(value.length <= 9) { "Color value must be no longer than 9 characters (includes #)" }
    }

    val toColorDrawable: ColorDrawable
        get() = ColorDrawable(Color.parseColor(value))

    val toAndroidColor: Int
        get() = Color.parseColor(value)

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?) = other.toString() == value

    override fun toString() = value

    companion object {
        val DEFAULT = WaqtiColor("#FFFFFF")
        val CARD_DEFAULT = WaqtiColor("#E0E0E0")
        val WAQTI_DEFAULT = WaqtiColor("#880E4F")
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun colorToHex(androidColor: Int) = String.format("#%06X", 0xFFFFFF and androidColor)

inline val String.toColor: WaqtiColor
    get() = WaqtiColor(this)

inline val Int.toColor: WaqtiColor
    get() = WaqtiColor(colorToHex(this))