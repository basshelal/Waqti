package uk.whitecrescent.waqti.frontend

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter

class MainActivityViewModel : ViewModel() {

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardListPosition = ChangedPositionPair(false, 0)
    var boardPosition = ChangedPositionPair(false, 0)
    var boardAdapter: BoardAdapter? = null
    var settingsChanged = false

}