@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_list.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.addAfterTextChangedListener
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
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

        setUpAppBar()

        setUpButton()

    }

    private inline fun setUpAppBar() {
        listName_editText.apply {
            requestFocusAndShowSoftKeyboard()
            mainActivity.hideableEditTextView = this
            addAfterTextChangedListener {
                if (it != null) {
                    addList_button.isVisible = !(it.isEmpty() || it.isBlank())
                }
            }
        }
    }

    private inline fun setUpButton() {
        addList_button.apply {
            isVisible = false
            setOnClickListener {
                Caches.boards[boardID].add(createElement()).update()
                finish()
            }
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
