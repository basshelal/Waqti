package uk.whitecrescent.waqti.frontend

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode
import uk.whitecrescent.waqti.frontend.fragments.view.ViewMode

/**
 * A way to access the [SharedPreferences] of Waqti by using type-aware variables.
 * All [SharedPreferences] used in Waqti will be found here.
 *
 * You should only have one instance of this class at any given point and that instance will be
 * found in [MainActivity] called [MainActivity.waqtiPreferences].
 *
 * @author Bassam Helal
 */
class WaqtiPreferences(val context: Context) {

    // The preferences keys, they're long for a reason, there's no use for them outside this class
    private val WAQTI_SHARED_PREFERENCES = "WaqtiSharedPreferences"
    val BOARD_LIST_NAME_PREFERENCES_KEY = "BoardListName"
    val BOARD_LIST_VIEW_MODE_PREFERENCES_KEY = "BoardListViewMode"
    val TASK_LIST_WIDTH_PREFERENCES_KEY = "TaskListWidth"
    val TASK_CARD_TEXT_SIZE_PREFERENCES_KEY = "TaskCardTextSize"
    val BOARD_SCROLL_SNAP_MODE_PREFERENCES_KEY = "BoardScrollSnapMode"

    val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(WAQTI_SHARED_PREFERENCES, Context.MODE_PRIVATE)


    // TODO: 29-Jun-19 Remove this and let us use the BoardList.name property
    inline var boardListName: String
        set(value) = sharedPreferences.edit {
            putString(BOARD_LIST_NAME_PREFERENCES_KEY, value)
        }
        get() = sharedPreferences.getString(
                BOARD_LIST_NAME_PREFERENCES_KEY,
                context.getString(R.string.allBoards))!!

    inline var boardListViewMode: ViewMode
        set(value) = sharedPreferences.edit {
            putString(BOARD_LIST_VIEW_MODE_PREFERENCES_KEY, value.name)
        }
        get() = ViewMode.valueOf(sharedPreferences.getString(
                BOARD_LIST_VIEW_MODE_PREFERENCES_KEY,
                ViewMode.LIST_VERTICAL.name)!!)

    inline var taskListWidth: Int
        set(value) = sharedPreferences.edit {
            putInt(TASK_LIST_WIDTH_PREFERENCES_KEY, value)
        }
        get() = sharedPreferences.getInt(
                TASK_LIST_WIDTH_PREFERENCES_KEY,
                66)

    inline var taskCardTextSize: Int
        set(value) = sharedPreferences.edit {
            putInt(TASK_CARD_TEXT_SIZE_PREFERENCES_KEY, value)
        }
        get() = sharedPreferences.getInt(
                TASK_CARD_TEXT_SIZE_PREFERENCES_KEY,
                18)

    inline var boardScrollSnapMode: ScrollSnapMode
        set(value) = sharedPreferences.edit {
            putString(BOARD_SCROLL_SNAP_MODE_PREFERENCES_KEY, value.name)
        }
        get() = ScrollSnapMode.valueOf(sharedPreferences.getString(
                BOARD_SCROLL_SNAP_MODE_PREFERENCES_KEY,
                ScrollSnapMode.PAGED.name)!!)

}