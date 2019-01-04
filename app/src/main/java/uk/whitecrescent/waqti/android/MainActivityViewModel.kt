package uk.whitecrescent.waqti.android

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.ID

class MainActivityViewModel : ViewModel() {

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardPosition = false to 0
    var boardListPosition = false to 0

    var createdTaskTime: Time = DEFAULT_TIME

}