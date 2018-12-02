package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.checkWritePermission
import uk.whitecrescent.waqti.android.customview.KBoardView
import uk.whitecrescent.waqti.android.customview.KDragItemAdapter
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.toArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Database.clearAllDBs().commit()

        checkWritePermission()

        boardView.snapToColumnWhenScrolling = true
        boardView.snapToColumnWhenDragging = true
        boardView.setSnapDragItemToTouch(true)
        boardView.snapToColumnInLandscape = false
        boardView.snapPosition = (KBoardView.ColumnSnapPosition.CENTER)

        class ViewHolderX(view: View) : KDragItemAdapter.ViewHolder(view, view.id, false)

        class Adapter : KDragItemAdapter<String, ViewHolderX>() {

            override val itemList: MutableList<String> =
                    (1..100).map { Task("Task number $it").toString() }.toArrayList

            override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolderX {
                return ViewHolderX(LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false))
            }

            override fun onBindViewHolder(holder: ViewHolderX, position: Int) {
                super.onBindViewHolder(holder, position)
                (holder.itemView as TextView).text = itemList[position]
            }

            override fun getUniqueItemId(position: Int): Long {
                return position.toLong()
            }
        }

        boardView.addColumn(Adapter(), null, null, false)
        boardView.addColumn(Adapter(), null, null, false)
        boardView.addColumn(Adapter(), null, null, false)
        boardView.addColumn(Adapter(), null, null, false)
        boardView.addColumn(Adapter(), null, null, false)

        //setUpBoard(board_recyclerView, this)

    }
}

/*
class ListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class BoardAdapter : RecyclerView.Adapter<ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.task_list, parent, false))
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holderList: ListViewHolder, position: Int) {
        (holderList.view as RecyclerView).apply {
            layoutManager = LinearLayoutManager(holderList.view.context)
            adapter = ListAdapter(0)
        }
    }

}

class ListItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class ListAdapter(val listId: ID) : RecyclerView.Adapter<ListItemViewHolder>() {

    val list = Array(100, { "String number $it" })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holderList: ListItemViewHolder, position: Int) {
        holderList.view.task_cardView.text = list[position]
    }

}

fun setUpBoard(boardView: RecyclerView, activity: Activity) {
    boardView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

    boardView.adapter = BoardAdapter()
}

fun seedDB() {
    if (Database.tasks.isEmpty()) Array(50, { Task("Auto Generated Task $it") })
    if (Database.taskLists.isEmpty()) TaskList("Task List 1", Caches.tasks.toList())
    Caches.allCaches.forEach { it.clearMap() }
}

fun clearDB() {
    Caches.clearAllCaches().commit()
    Database.clearAllDBs().commit()
}*/
