package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.checkWritePermission
import uk.whitecrescent.waqti.android.customview.BoardAdapter
import uk.whitecrescent.waqti.android.logE
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.isEmpty
import uk.whitecrescent.waqti.model.persistence.size
import uk.whitecrescent.waqti.model.task.Task


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkWritePermission()

        if (Database.boards.isEmpty()) Database.boards.put(Board("Test Board"))

        "Board showing is ID: ${Database.boards.all.first().id}".logE()

        boardView.adapter = BoardAdapter(Database.boards.all.first().id)

        supportActionBar?.title =
                "Waqti - ${boardView.boardAdapter.board.name} ${boardView.boardAdapter.boardID}"

        add_button.setOnClickListener {
            boardView.addNewEmptyList("New List @$now")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Caches.close()
    }
}

fun seedDB() {
    if (Database.tasks.size < 500)
        (0..100).forEach { Task("Auto Generated Task $it") }
    if (Database.taskLists.isEmpty()) TaskList("Task List 1", Caches.tasks.toList())
}

fun clearDB() {
    Caches.clearAllCaches().commit()
    Database.clearAllDBs().commit()
}
