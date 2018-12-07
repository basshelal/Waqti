package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.checkWritePermission
import uk.whitecrescent.waqti.android.goToActivity
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

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TaskAdapter()

        add_button.setOnClickListener {
            goToActivity(CreateTaskActivity::class.java)
            Caches.tasks.put(Task("New Task @ $now"))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        closeCache()
    }
}

fun closeCache() {
    Caches.allCaches.forEach { it.close() }
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
