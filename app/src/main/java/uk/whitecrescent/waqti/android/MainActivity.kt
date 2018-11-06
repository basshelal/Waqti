package uk.whitecrescent.waqti.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.objectbox.kotlin.boxFor
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.Database
import uk.whitecrescent.waqti.model.logE
import uk.whitecrescent.waqti.model.task.Task

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter()

        Database.store.boxFor<Task>().put(Array<Task>(100, { Task("TEST!") }).toList())

        logE(Database.store.boxFor<Task>().count())
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    val db = Database.store.boxFor<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false))
    }

    override fun getItemCount() = db.count().toInt()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).text = db[position.toLong() + 1].toString()
    }

}
