package uk.whitecrescent.waqti.frontend

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode
import uk.whitecrescent.waqti.frontend.fragments.view.ViewMode

class WaqtiPreferences(val context: Context) {

    val WAQTI_SHARED_PREFERENCES = "WaqtiSharedPreferences"
    val BOARD_LIST_NAME_PREFERENCES_KEY = "BoardListName"
    val BOARD_LIST_VIEW_MODE_KEY = "BoardListViewMode"
    val TASK_LIST_WIDTH_KEY = "TaskListWidth"
    val TASK_CARD_TEXT_SIZE = "TaskCardTextSize"
    val BOARD_SCROLL_SNAP_MODE = "BoardScrollSnapMode"

    val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(WAQTI_SHARED_PREFERENCES, Context.MODE_PRIVATE)


    inline var boardListName: String
        set(value) = sharedPreferences.edit {
            putString(BOARD_LIST_NAME_PREFERENCES_KEY, value)
        }
        get() = sharedPreferences.getString(
                BOARD_LIST_NAME_PREFERENCES_KEY,
                context.getString(R.string.allBoards))!!

    inline var boardListViewMode: ViewMode
        set(value) = sharedPreferences.edit {
            putString(BOARD_LIST_VIEW_MODE_KEY, value.name)
        }
        get() = ViewMode.valueOf(sharedPreferences.getString(
                BOARD_LIST_VIEW_MODE_KEY,
                ViewMode.LIST_VERTICAL.name)!!)


    inline var taskListWidth: Int
        set(value) = sharedPreferences.edit {
            putInt(TASK_LIST_WIDTH_KEY, value)
        }
        get() = sharedPreferences.getInt(
                TASK_LIST_WIDTH_KEY,
                66)

    inline var taskCardTextSize: Int
        set(value) = sharedPreferences.edit {
            putInt(TASK_CARD_TEXT_SIZE, value)
        }
        get() = sharedPreferences.getInt(
                TASK_CARD_TEXT_SIZE,
                18)

    inline var boardScrollSnapMode: ScrollSnapMode
        set(value) = sharedPreferences.edit {
            putString(BOARD_SCROLL_SNAP_MODE, value.name)
        }
        get() = ScrollSnapMode.valueOf(sharedPreferences.getString(
                BOARD_SCROLL_SNAP_MODE,
                ScrollSnapMode.PAGED.name)!!)

}