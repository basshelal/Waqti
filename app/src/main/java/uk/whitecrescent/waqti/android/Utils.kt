@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.Task

inline fun Activity.shortSnackbar(view: View, string: CharSequence) =
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()

inline fun Activity.checkWritePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

    }
}

inline fun addTasks(amount: Int) {
    Database.tasks.put(
            Array(amount, { Task("Auto Generated Task #$it") }).toList())
}