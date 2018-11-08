package uk.whitecrescent.waqti.model.persistence

import android.content.Context
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import uk.whitecrescent.waqti.model.Committable
import uk.whitecrescent.waqti.model.MyObjectBox
import uk.whitecrescent.waqti.model.TestEntity
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import java.io.File

// A build function must be invoked before using anything else here! consider it the constructor
// or init of this object
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
    lateinit var testEntityDB: Box<TestEntity>
        private set
    lateinit var allDBs: List<Box<*>>
        private set

    // TODO: 08-Nov-18 make Database Queries on each box be asynchronous using Rx possibly

    fun build(context: Context) {
        store = MyObjectBox.builder().androidContext(context.applicationContext).build()
        build()
    }

    fun buildTest(directory: File) {
        store = MyObjectBox.builder().directory(directory).build()
        build()
    }

    fun build() {
        taskDB = store.boxFor()
        templateDB = store.boxFor()
        labelDB = store.boxFor()
        priorityDB = store.boxFor()
        timeUnitDB = store.boxFor()
        testEntityDB = store.boxFor()
        allDBs = listOf(
                taskDB, templateDB, labelDB, priorityDB, timeUnitDB, testEntityDB
        )
    }

    fun clearAllDBs(): Committable {
        return object : Committable {
            override fun commit() {
                allDBs.forEach { it.removeAll() }
            }
        }
    }

}