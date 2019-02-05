package uk.whitecrescent.waqti.model.persistence

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import uk.whitecrescent.waqti.model.Committable
import uk.whitecrescent.waqti.model.MyObjectBox
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.BoardList
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import java.io.File
import java.io.FileWriter

// A build function must be invoked before using anything else here! consider it the constructor
// or init of this object
// For doing anything Persistence related it is best to do it with its respective Cache!
object Database {

    lateinit var store: BoxStore
        private set
    lateinit var allDBs: List<Box<*>>
        private set

    // Tasks

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

    // Collections

    lateinit var taskLists: Box<TaskList>
        private set
    lateinit var boards: Box<Board>
        private set
    lateinit var boardLists: Box<BoardList>
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
        taskLists = store.boxFor()
        boards = store.boxFor()
        boardLists = store.boxFor()
        allDBs = listOf(
                tasks, templates, labels, priorities, timeUnits, taskLists, boards, boardLists
        )
    }

    fun clearAllDBs(): Committable {
        return object : Committable {
            override fun commit() {
                allDBs.forEach { it.removeAll() }
            }
        }
    }

    fun export(exportedFile: File): File {
        require(::store.isInitialized)
        val gson = Gson()
        val writer = JsonWriter(FileWriter(exportedFile))
        val tasksType = object : TypeToken<MutableList<Task>>() {}.type

        gson.toJson(tasks.all, tasksType, writer)
        writer.close()
        return exportedFile
    }

    fun import(file: File) {
        // import from the massive JSON file
        TODO()
    }

    fun repair(): Boolean {
        TODO()
    }

}