@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_list.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.frontend.CREATE_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragmentViewModel
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.requestFocusAndShowKeyboard
import uk.whitecrescent.waqti.scrollToEnd

class CreateListFragment : WaqtiCreateFragment<TaskList>() {

    override lateinit var viewModel: CreateListFragmentViewModel

    var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = mainActivityVM.boardID

        viewModel = getViewModel()

        setUpViews()

    }

    override fun setUpViews() {

        setUpAppBar()

        setUpButton()

    }

    override fun setUpAppBar() {
        mainActivity.appBar {
            backgroundColor = WaqtiColor.TRANSPARENT.toAndroidColor
            elevation = 0F
            leftImageView.isVisible = false
            editTextView {
                textColor = WaqtiColor.BLACK.toAndroidColor
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.listNameHint)
                text = SpannableStringBuilder("")
                requestFocusAndShowKeyboard()
                addAfterTextChangedListener {
                    if (it != null) {
                        addList_button.isVisible = !(it.isEmpty() || it.isBlank())
                    }
                }
            }
            rightImageView.isVisible = false
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
        return TaskList(mainActivity.appBar.editTextView.text.toString())
    }

    override fun finish() {
        mainActivityVM.boardAdapter?.onInflate = {
            scrollToEnd()
        }

        mainActivityVM.boardPosition
                .changeTo(true to mainActivityVM.boardPosition.position + 1)
        @FragmentNavigation(from = CREATE_LIST_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }
}

class CreateListFragmentViewModel : WaqtiCreateFragmentViewModel<TaskList>() {

    override fun createElement(fromFragment: TaskList): TaskList {
        TODO()
    }

}
