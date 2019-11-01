@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.backend.persistence

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.MissingFeature
import uk.whitecrescent.waqti.backend.Committable
import uk.whitecrescent.waqti.backend.MyObjectBox
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Database.ImportMethod.ADD
import uk.whitecrescent.waqti.backend.persistence.Database.ImportMethod.MERGE
import uk.whitecrescent.waqti.backend.persistence.Database.ImportMethod.OVERRIDE
import uk.whitecrescent.waqti.backend.persistence.Database.ImportMethod.REPLACE
import uk.whitecrescent.waqti.backend.task.Label
import uk.whitecrescent.waqti.backend.task.Priority
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.backend.task.Template
import uk.whitecrescent.waqti.backend.task.TimeUnit
import uk.whitecrescent.waqti.extensions.size
import uk.whitecrescent.waqti.now
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
    }

    fun clearAllDBs() = Committable {
        allDBs.forEach { it.removeAll() }
    }

    fun needsRepair(): Boolean {
        return when {
            boardLists.size != 1 -> true
            boards.all.filter { it !in boardLists.all.first() }.isNotEmpty() -> true
            taskLists.all.filter { it !in boards.all.flatMap { it.toList() } }.isNotEmpty() -> true
            tasks.all.filter { it !in taskLists.all.flatMap { it.toList() } }.isNotEmpty() -> true
            else -> false
        }
    }

    fun solveBoardList() {
        (Database.boardLists.size).also {
            if (it == 1) return
            if (it == 0) {
                Database.boardLists.put(BoardList("All Boards"))
                Database.boardLists.all.first().addAll(Caches.boards.all())
            }
            if (it > 1) {
                Database.boardLists.removeAll()
                Database.boardLists.put(BoardList("All Boards"))
                Database.boardLists.all.first().addAll(Caches.boards.all())
            }
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
        REPLACE, ADD, MERGE, OVERRIDE
    }

    class ImportException(message: String) : IllegalStateException(message)

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
            OVERRIDE -> {

            }
        }
        reader.close()
    }

}