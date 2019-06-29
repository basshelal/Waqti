@file:Suppress("NOTHING_TO_INLINE", "DEPRECATION")

package uk.whitecrescent.waqti.frontend

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

// Extensions for compatibility so that we don't have to write these guards every time

inline fun Vibrator.vibrateCompat(millis: Long) {
    if (Build.VERSION.SDK_INT >= 26) {
        vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrate(millis)
    }
}
