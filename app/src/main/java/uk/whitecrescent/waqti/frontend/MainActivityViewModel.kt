package uk.whitecrescent.waqti.frontend

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter

class MainActivityViewModel : ViewModel() {

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardListPosition = ChangedPositionPair()

    // TODO: 27-Jun-19 We need to get rid of this guy sometime
    var boardPosition = ChangedPositionPair()
    var boardAdapter: BoardAdapter? = null
    var settingsChanged = false

}