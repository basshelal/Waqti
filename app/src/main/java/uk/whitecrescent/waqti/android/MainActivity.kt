package uk.whitecrescent.waqti.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.logE
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.Task

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkWritePermission()

        logE(Database.tasks.count())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter()

        // use BoardView for Boards
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    val db = Database.tasks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false))
    }

    override fun getItemCount() = db.count().toInt()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).text = db[position.toLong() + 1].toString()
    }

}

fun addTasks(amount: Int) {
    Database.tasks.put(Array(amount, { Task() }).toList())
}
