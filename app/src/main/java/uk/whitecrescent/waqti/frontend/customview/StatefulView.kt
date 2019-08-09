package uk.whitecrescent.waqti.frontend.customview

import android.view.View
import uk.whitecrescent.waqti.frontend.customview.StatefulView.ViewState

/**
 * Custom [View]s that have any sort of complex state management should implement this
 * interface.
 *
 * A [ViewState] object is used to manage the state of the UI which will then be updated in
 * [updateState] which will in turn apply the new changes to the state and update the UI
 * accordingly.
 *
 * Your custom [ViewState] type should be the type parameter [S] of this [StatefulView]
 */
interface StatefulView<S : StatefulView.ViewState> {

    /** The state of this [View] */
    val state: S

    /** Updates the [apply] block to the [state] of this [View]. */
    fun updateState(apply: S.() -> Unit): View

    /** Updates the UI to match the [state] */
    fun updateUI(): View

    /** Extend this to provide your own State variables and functions. */
    abstract class ViewState

}