package uk.whitecrescent.waqti.model.persistence

import android.content.Context
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.MyObjectBox
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import java.io.File

object Database {

    lateinit var store: BoxStore
        private set
    lateinit var taskDB: Box<Task>
        private set
    lateinit var templateDB: Box<Template>
        private set
    lateinit var labelDB: Box<Label>
        private set
    lateinit var priorityDB: Box<Priority>
        private set
    lateinit var timeUnitDB: Box<TimeUnit>
        private set

    fun build(context: Context) {
        store = MyObjectBox.builder().androidContext(context.applicationContext).build()
        taskDB = store.boxFor()
        templateDB = store.boxFor()
        labelDB = store.boxFor()
        priorityDB = store.boxFor()
        timeUnitDB = store.boxFor()
    }

    fun buildTest(directory: File) {
        store = MyObjectBox.builder().directory(directory).build()
        taskDB = store.boxFor()
        templateDB = store.boxFor()
        labelDB = store.boxFor()
        priorityDB = store.boxFor()
        timeUnitDB = store.boxFor()
    }
}