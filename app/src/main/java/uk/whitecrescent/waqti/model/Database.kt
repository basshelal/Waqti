package uk.whitecrescent.waqti.model

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import uk.whitecrescent.waqti.model.task.MyObjectBox
import uk.whitecrescent.waqti.model.task.Task
import java.io.File

object Database {
    val store: BoxStore = MyObjectBox.builder().directory(File("DEBUG_DB")).build()
    val taskDB = store.boxFor<Task>()
//    val templateDB = store.boxFor<Template>()
//    val labelDB = store.boxFor<Label>()
//    val priorityDB = store.boxFor<Priority>()
//    val timeUnitDB = store.boxFor<TimeUnit>()

    fun deleteDB() {
        store.apply {
            close()
            deleteAllFiles()
        }
    }
}