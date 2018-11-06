package uk.whitecrescent.waqti.model

import android.content.Context
import io.objectbox.BoxStore
import uk.whitecrescent.waqti.model.task.MyObjectBox

object Database {

    lateinit var store: BoxStore

    fun build(context: Context) {
        store = MyObjectBox.builder().androidContext(context.applicationContext).build()
    }
//    val taskDB = store.boxFor<Task>()
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