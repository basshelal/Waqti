@file:Suppress("NOTHING_TO_INLINE", "DEPRECATION")

package uk.whitecrescent.waqti.frontend

import android.content.ClipData
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import android.widget.TimePicker

// Extensions for compatibility so that we don't have to write these guards every time

inline fun TextView.setTextAppearanceCompat(resId: Int) {
    setTextAppearance(resId)
}

inline fun Resources.getColorCompat(colorRes: Int, theme: Resources.Theme? = null): Int {
    return getColor(colorRes, theme)
}

inline fun Context.getColorCompat(resId: Int): Int {
    return resources.getColorCompat(resId)
}

inline fun Vibrator.vibrateCompat(millis: Long) {
    if (Build.VERSION.SDK_INT >= 26) {
        vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrate(millis)
    }
}

inline fun View.startDragCompat(data: ClipData?, shadowBuilder: View.DragShadowBuilder,
                                myLocalState: Any?, flags: Int): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        startDragAndDrop(data, shadowBuilder, myLocalState, flags)
    } else {
        startDrag(data, shadowBuilder, myLocalState, flags)
    }
}

inline var TimePicker.hourCompat: Int
    set(value) {
        hour = value
    }
    get() = hour

inline var TimePicker.minuteCompat: Int
    set(value) {
        minute = value
    }
    get() = minute
