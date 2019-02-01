package uk.whitecrescent.waqti.android

import androidx.lifecycle.ViewModel
import uk.whitecrescent.waqti.Bug
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.model.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.ID

class MainActivityViewModel : ViewModel() {

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardPosition = false to 0
    var boardListPosition = false to 0

    @ForLater
    // TODO: 01-Feb-19 Migrate to individual Fragment's ViewModels rather than global ViewModel

    @Bug
    // TODO: 17-Jan-19 We need to re-set these to default properly
    // try creating a new task, setting its stuff then cancelling, these values will appear on
    // the next task's properties
    var createdTaskTime: Time = DEFAULT_TIME
    var createdTaskDeadline: Time = DEFAULT_TIME
    var createdTaskDescription: String = DEFAULT_DESCRIPTION

}