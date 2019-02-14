package uk.whitecrescent.waqti.model

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

open class Background(val value: String)

class ColorBackground(value: String) : Background(value) {

    val colorDrawable: ColorDrawable
        get() = ColorDrawable(Color.parseColor(value))

}

class ImageBackground(value: String) : Background(value)