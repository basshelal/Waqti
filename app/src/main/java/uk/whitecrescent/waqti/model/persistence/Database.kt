package uk.whitecrescent.waqti.model.persistence

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.MissingFeature
import uk.whitecrescent.waqti.forEach
import uk.whitecrescent.waqti.model.Committable
import uk.whitecrescent.waqti.model.MyObjectBox
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.BoardList
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Database.ImportMethod.ADD
import uk.whitecrescent.waqti.model.persistence.Database.ImportMethod.MERGE
import uk.whitecrescent.waqti.model.persistence.Database.ImportMethod.REPLACE
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import uk.whitecrescent.waqti.now
import uk.whitecrescent.waqti.size
import java.io.File
import java.io.FileReader
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
        Caches.initialize()
    }

    fun clearAllDBs(): Committable {
        return object : Committable {
            override fun commit() {
                allDBs.forEach { it.removeAll() }
            }
        }
    }

    fun applyMigration() {
        boards.forEach {
            if (it.backgroundValue != "#FFFFFF")
                it.backgroundValue = "#FFFFFF"
        }
    }

    @MissingFeature
    @ForLater
    fun repair(): Boolean {
        if (boardLists.size < 1) {
            BoardList("Default").addAll(boards.all).update()
            return true
        }
        if (boardLists.size > 1) {
            val boardList = boardLists.all.first()
            val illegalBoardLists = boardLists.all.filter { it != boardList }
            boardList.addAll(illegalBoardLists.flatMap { it.toList() }).update()
            boardLists.remove(illegalBoardLists)
            return true
        }

        val boardList = boardLists.all.first()
        val illegalBoards = boards.all.filter { it !in boardList }
        if (illegalBoards.isNotEmpty()) {
            boardList.addAll(illegalBoards).update()
            boards.remove(illegalBoards)
            return true
        }

        val illegalTaskLists = taskLists.all.filter { it !in boards.all.flatMap { it.toList() } }
        if (illegalTaskLists.isNotEmpty()) {
            Board("Repair $now").addAll(illegalTaskLists).update()
            taskLists.remove(illegalTaskLists)
            return true
        }

        val illegalTasks = tasks.all.filter { it !in taskLists.all.flatMap { it.toList() } }
        if (illegalTasks.isNotEmpty()) {
            val repairBoard = Board("Repair $now")
            val repairList = TaskList("Repair $now")
            repairBoard.add(repairList).update()
            repairList.addAll(illegalTasks).update()
            tasks.remove(illegalTasks)
            return true
        }

        // reached here then nothing was repaired
        return false
    }

    enum class ImportMethod {
        REPLACE, ADD, MERGE
    }

    class ImportException(message: String) : IllegalStateException(message)

    @FutureIdea
    // TODO: 04-Feb-19 Import and export Database like what Nova Launcher does
    // you can export the Database to be shared to something like email, drive etc and thus
    // you can also import a Database, doing so will probably delete all current data but we
    // might be able to allow a merge like what Chrome does when importing bookmarks, just
    // adds them to what's already existing, options for which one the user would like is
    // probably the way to go

    @MissingFeature
    @ForLater
    fun export(exportedFile: File): File {
        require(::store.isInitialized)
        val gson = Gson()
        val writer = JsonWriter(FileWriter(exportedFile))

        gson.toJson(boardLists.all.first(), object : TypeToken<BoardList>() {}.type,
                writer)

        writer.close()
        return exportedFile
    }

    @MissingFeature
    @ForLater
    fun import(importedFile: File, importMethod: ImportMethod) {
        require(::store.isInitialized)
        val gson = Gson()
        val reader = FileReader(importedFile)

        val boardList = gson.fromJson<BoardList>(
                reader, object : TypeToken<BoardList>() {}.type
        )

        when (importMethod) {
            REPLACE -> {
                Database.clearAllDBs().commit()
                Database.boardLists.put(boardList)
            }
            ADD -> {

            }
            MERGE -> {

            }
        }
        reader.close()
    }

}