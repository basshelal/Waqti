@file:Suppress("NOTHING_TO_INLINE", "DEPRECATION")

package uk.whitecrescent.waqti.android

import android.content.ClipData
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import android.widget.TimePicker

inline fun TextView.setTextAppearanceCompat(resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(this.context, resId)
    }
}

inline fun Resources.getColorCompat(colorRes: Int, theme: Resources.Theme? = null): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(colorRes, theme)
    } else {
        getColor(colorRes)
    }
}

inline fun Vibrator.vibrateCompat(millis: Long) {
    if (Build.VERSION.SDK_INT >= 26) {
        vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrate(millis)
    }
}

inline fun View.startDragCompat(data: ClipData, shadowBuilder: View.DragShadowBuilder,
                                myLocalState: Any?, flags: Int): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        startDragAndDrop(data, shadowBuilder, myLocalState, flags)
    } else {
        startDrag(data, shadowBuilder, myLocalState, flags)
    }
}

inline var TimePicker.hourCompat: Int
    set(value) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hour = value
    } else {
        currentHour = value
    }
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hour
    } else {
        currentHour
    }

inline var TimePicker.minuteCompat: Int
    set(value) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        minute = value
    } else {
        currentMinute = value
    }
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        minute
    } else {
        currentMinute
    }