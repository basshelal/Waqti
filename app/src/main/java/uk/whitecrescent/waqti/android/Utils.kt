@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.android

import android.app.Activity
import android.support.design.widget.Snackbar
import android.view.View

inline fun Activity.shortSnackbar(view: View, string: CharSequence) =
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()