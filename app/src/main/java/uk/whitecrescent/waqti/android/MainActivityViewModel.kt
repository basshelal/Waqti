package uk.whitecrescent.waqti.android

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.model.task.ID

class MainActivityViewModel : ViewModel() {

    @ForLater
    // TODO: 01-Feb-19 Migrate to individual Fragment's ViewModels rather than global ViewModel

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardPosition = false to 0
    var boardListPosition = false to 0

}