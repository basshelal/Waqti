package uk.whitecrescent.waqti.android.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_list.*
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.requestFocusAndShowSoftKeyboard

class CreateListFragment : WaqtiCreateFragment<TaskList>() {

    var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = mainActivityViewModel.boardID

        setUpViews()

    }

    override fun setUpViews() {
        listName_editText.requestFocusAndShowSoftKeyboard()

        listName_editText.addAfterTextChangedListener {
            if (it != null) {
                addList_button.isVisible = !(it.isEmpty() || it.isBlank())
            }
        }

        addList_button.isVisible = false
        addList_button.setOnClickListener {
            Caches.boards[boardID].add(createElement()).update()
            finish()
        }
    }

    override fun createElement(): TaskList {
        return TaskList(listName_editText.text.toString())
    }

    override fun finish() {
        listName_editText.hideSoftKeyboard()
        mainActivityViewModel.boardPosition = true to mainActivityViewModel.boardPosition.second + 1
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
