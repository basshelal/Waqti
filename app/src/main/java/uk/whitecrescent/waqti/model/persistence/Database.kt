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
    lateinit var tasks: Box<Task>
        private set
    lateinit var templates: Box<Template>
        private set
    lateinit var labels: Box<Label>
        private set
    lateinit var priorities: Box<Priority>
        private set
    lateinit var timeUnits: Box<TimeUnit>
        private set
    lateinit var testEntities: Box<TestEntity>
        private set
    lateinit var allDBs: List<Box<*>>
        private set


    fun build(context: Context) {
        store = MyObjectBox.builder().androidContext(context.applicationContext).build()
        build()
    }

    fun buildTest(directory: File) {
        store = MyObjectBox.builder().directory(directory).build()
        build()
    }

    fun build() {
        tasks = store.boxFor()
        templates = store.boxFor()
        labels = store.boxFor()
        priorities = store.boxFor()
        timeUnits = store.boxFor()
        testEntities = store.boxFor()
        allDBs = listOf(
                tasks, templates, labels, priorities, timeUnits, testEntities
        )
    }

    inline fun <reified T> put(vararg elements: T) {
        store.boxFor<T>().put(*elements)
    }

//    inline fun <reified T> get(element: T): T? {
//        return store.boxFor<T>().all.firstOrNull { it == element }
//    }

    fun clearAllDBs(): Committable {
        return object : Committable {
            override fun commit() {
                allDBs.forEach { it.removeAll() }
            }
        }
    }

}