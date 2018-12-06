package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.checkWritePermission
import uk.whitecrescent.waqti.android.customview.KBoardView
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.isEmpty
import uk.whitecrescent.waqti.model.task.Task

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkWritePermission()

        seedDB()
        loadCache()


        boardView.snapToColumnWhenScrolling = true
        boardView.snapToColumnWhenDragging = true
        boardView.snapToColumnInLandscape = false
        boardView.snapPosition = (KBoardView.ColumnSnapPosition.CENTER)
        boardView.dragEnabled = true

        boardView.addColumn(TaskAdapter(), null, null, false)
        boardView.addColumn(TaskAdapter(), null, null, false)

        /*boardView.snapToColumnWhenScrolling = true
        boardView.snapToColumnWhenDragging = true
        boardView.setSnapDragItemToTouch(true)
        boardView.snapToColumnInLandscape = false
        boardView.snapPosition = (KBoardView.ColumnSnapPosition.CENTER)

        class ViewHolderX(view: View) : KDragItemAdapter.ViewHolder(view, view.id, false)

        class Adapter : KDragItemAdapter<String, ViewHolderX>() {

            override val itemList: MutableList<String> =
                    mutableListOf(*Array(100, { "Text number $it" }))

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
        boardView.addColumn(Adapter(), null, null, false)*/

    }

    override fun onDestroy() {
        super.onDestroy()
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
}*/

fun loadCache() {
    // This should be done asynchronously using Rx and have the View update asynchronously as
    // well so that the initial load time isn't awful like it is right now
    if (!Database.tasks.isEmpty()) Caches.tasks.put(Database.tasks.all)
}

fun closeCache() {
    Caches.allCaches.forEach { it.close() }
}

fun seedDB() {
    if (Database.tasks.isEmpty()) Array(50, { Task("Auto Generated Task $it") })
    if (Database.taskLists.isEmpty()) TaskList("Task List 1", Caches.tasks.toList())
    //Caches.allCaches.forEach { it.clearMap() }
}

fun clearDB() {
    Caches.clearAllCaches().commit()
    Database.clearAllDBs().commit()
}
