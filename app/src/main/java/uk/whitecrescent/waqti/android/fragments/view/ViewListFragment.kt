package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_list.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class ViewListFragment : WaqtiViewFragment() {

    companion object {
        fun newInstance() = ViewListFragment()
    }

    var listID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        listID = viewModel.listID

        setUpViews(Caches.taskLists[listID])
    }

    private fun setUpViews(taskList: TaskList) {
        mainActivity.supportActionBar?.title = "List"
        listName_textView.text = taskList.name
    }
}
