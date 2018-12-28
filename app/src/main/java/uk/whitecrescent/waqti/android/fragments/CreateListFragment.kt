package uk.whitecrescent.waqti.android.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_list.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.fragments.base.WaqtiCreateFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.showSoftKeyboard
import uk.whitecrescent.waqti.model.Inconvenience
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class CreateListFragment : WaqtiCreateFragment() {

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

        if (arguments != null) {
            boardID = arguments!!["boardID"] as ID
        }

        focusListNameTextView()

        setUpButtonOnClick()

    }

    private fun focusListNameTextView() {
        listName_editText.apply {
            requestFocus()
            showSoftKeyboard()
        }
    }

    private fun setUpButtonOnClick() {
        addList_button.setOnClickListener {
            val text = listName_editText.text
            if (text == null || text.isEmpty() || text.isBlank()) {
                textIsIncorrect()
            } else {
                textIsCorrect()
            }
        }

        dev_addList_button.setOnClickListener {
            Caches.boards[boardID]
                    .add(TaskList("Dev TaskList")).update()
            @Inconvenience // TODO: 28-Dec-18 BoardView scroll to the new list's position 
            finalize()
        }
    }

    private fun textIsIncorrect() {
        listName_editText.setHintTextColor(Color.RED)
        listName_editText.hint = "List name cannot be empty!"
    }

    private fun textIsCorrect() {
        Caches.boards[boardID]
                .add(listFromEditText()).update()
        finalize()
    }

    private fun listFromEditText(): TaskList {
        return TaskList(listName_editText.text.toString())
    }

    private fun finalize() {
        listName_editText.hideSoftKeyboard()
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
