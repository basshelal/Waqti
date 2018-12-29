package uk.whitecrescent.waqti.android

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.model.ForLater
import uk.whitecrescent.waqti.model.task.ID

class MainActivityViewModel : ViewModel() {

    @ForLater
    // TODO: 28-Dec-18 These will be used instead of passing bundle arguments
    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardPosition: Int = -1

}