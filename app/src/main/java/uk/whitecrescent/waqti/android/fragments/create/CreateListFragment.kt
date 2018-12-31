package uk.whitecrescent.waqti.android.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_list.*
import uk.whitecrescent.waqti.BuildConfig
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.openKeyboard
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class CreateListFragment : WaqtiCreateFragment<TaskList>() {

    companion object {
        fun newInstance() = CreateListFragment()
    }

    var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = viewModel.boardID

        setUpViews()

    }

    override fun setUpViews() {
        if (!BuildConfig.DEBUG) dev_addList_button.visibility = View.GONE

        mainActivity.supportActionBar?.title = "New List"

        listName_editText.openKeyboard()

        listName_editText.addAfterTextChangedListener {
            if (it != null) {
                addList_button.isEnabled = !(it.isEmpty() || it.isBlank())
            }
        }

        addList_button.isEnabled = false
        addList_button.setOnClickListener {
            Caches.boards[boardID].add(createElement()).update()
            finish()
        }

        dev_addList_button.setOnClickListener {
            Caches.boards[boardID].add(TaskList("Dev TaskList")).update()
            finish()
        }
    }

    override fun createElement(): TaskList {
        return TaskList(listName_editText.text.toString())
    }

    override fun finish() {
        listName_editText.hideSoftKeyboard()
        viewModel.boardPosition = true to viewModel.boardPosition.second + 1
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
