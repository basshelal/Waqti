package uk.whitecrescent.waqti.frontend.appearance

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

data class WaqtiColor(val value: String) {
    init {
        require(value.startsWith('#')) { "Color must start with #" }
        require(value.length <= 9) { "Color value must be no longer than 9 characters (includes #)" }
    }

    val toColorDrawable: ColorDrawable
        get() = ColorDrawable(Color.parseColor(value))

    val toAndroidColor: Int
        get() = Color.parseColor(value)

    companion object {
        val DEFAULT = WaqtiColor("#FFFFFF")
        val CARD_DEFAULT = WaqtiColor("#E0E0E0")
        val WAQTI_DEFAULT = WaqtiColor("#880E4F")
    }
}

inline val String.toColor: WaqtiColor
    get() = WaqtiColor(this)

val COLORS_100 = listOf(
        "#FFFFFF", "#ffcdd2", "#f8bbd0", "#e1bee7", "#d1c4e9",
        "#c5cae9", "#bbdefb", "#b3e5fc", "#b2ebf2", "#b2dfdb",
        "#c8e6c9", "#dcedc8", "#f0f4c3", "#fff9c4", "#ffecb3",
        "#ffe0b2", "#ffccbc", "#d7ccc8", "#f5f5f5", "#cfd8dc"
).map { it.toColor }

val COLORS_200 = listOf(
        "#FFFFFF", "#ef9a9a", "#f48fb1", "#ce93d8", "#b39ddb",
        "#9fa8da", "#90caf9", "#81d4fa", "#80deea", "#80cbc4",
        "#a5d6a7", "#c5e1a5", "#e6ee9c", "#fff59d", "#ffe082",
        "#ffcc80", "#ffab91", "#bcaaa4", "#eeeeee", "#b0bec5"
).map { it.toColor }