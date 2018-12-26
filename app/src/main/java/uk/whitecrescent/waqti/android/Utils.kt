@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.Task

inline fun Activity.shortSnackbar(view: View, string: CharSequence) =
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()

inline fun Activity.longSnackbar(view: View, string: CharSequence) =
        Snackbar.make(view, string, Snackbar.LENGTH_LONG).show()

inline fun Activity.checkWritePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }
}

inline fun View.snackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_SHORT).show()

inline fun View.hideSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)
}

inline fun <A : Activity> Activity.goToActivity(activity: Class<A>) {
    this.startActivity(Intent(this, activity))
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