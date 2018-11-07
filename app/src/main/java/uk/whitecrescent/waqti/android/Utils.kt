@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View

inline fun Activity.shortSnackbar(view: View, string: CharSequence) =
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()

inline fun Activity.checkWritePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

    }
}