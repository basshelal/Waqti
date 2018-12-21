@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.android

import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar

inline fun View.red(predicate: () -> Boolean) {
    if (predicate()) this.setBackgroundColor(Color.RED)
}

inline fun View.red() = setBackgroundColor(Color.RED)

inline fun View.white() = setBackgroundColor(Color.WHITE)

inline fun View.longSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_LONG).show()

inline fun View.infSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_INDEFINITE).show()

inline fun String.logE() {
    Log.e("DEFAULT", this)
}

inline infix fun String.logE(tag: String) {
    Log.e(tag, this)
}

inline fun View.logPosition() {
    "x: ${this.x}, y: ${this.y}" logE "POSITION"
}

inline fun View.logLayout() {
    "w: ${this.width}, h: ${this.height}" logE "LAYOUT"
}