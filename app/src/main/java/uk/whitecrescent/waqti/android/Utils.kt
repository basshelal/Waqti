@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.android

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.Task

/**
 * Just a utility to show us where there are Fragment changes happening
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
annotation class GoToFragment

inline fun Activity.checkWritePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }
}

inline val Activity.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

inline val View.mainActivity: MainActivity
    get() = this.context as MainActivity

inline fun View.hideSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)
}

inline fun View.showSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, 0)
}

inline fun View.openKeyboard() {
    requestFocus()
    showSoftKeyboard()
}

inline fun View.closeKeyboard() {
    clearFocus()
    hideSoftKeyboard()
}

inline fun addTasks(amount: Int) {
    Database.tasks.put(
            Array(amount, { Task("Auto Generated Task #$it") }).toList())
}

inline fun RecyclerView.scrollToEnd() {
    if (this.adapter != null) {
        this.smoothScrollToPosition(adapter!!.itemCount - 1)
    }
}

inline fun RecyclerView.scrollToStart() {
    if (this.adapter != null) {
        this.smoothScrollToPosition(0)
    }
}