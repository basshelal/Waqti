package uk.whitecrescent.waqti.android

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.model.Bug
import uk.whitecrescent.waqti.model.task.ID

class MainActivityViewModel : ViewModel() {

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    @Bug // TODO: 29-Dec-18 Make sure this is updated properly whenever, few bugs here
    var boardPosition: Int = 0

}