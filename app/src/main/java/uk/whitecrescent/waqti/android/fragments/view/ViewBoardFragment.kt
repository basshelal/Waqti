package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_board_view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.CREATE_LIST_FRAGMENT
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialConfirmDialog
import uk.whitecrescent.waqti.android.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.android.fragments.create.CreateListFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.mainActivity
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class ViewBoardFragment : WaqtiViewFragment<Board>() {

    companion object {
        fun newInstance() = ViewBoardFragment()
    }

    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = viewModel.boardID

        setUpViews(Caches.boards[boardID])
    }

    override fun setUpViews(element: Board) {
        mainActivity.supportActionBar?.title =
                "Board - ${element.name} ${element.id} "

        boardName_editTextView.text = SpannableStringBuilder(element.name)
        boardName_editTextView.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (textView.text != null &&
                        textView.text.isNotBlank() &&
                        textView.text.isNotEmpty()) {
                    if (textView.text != element.name) {
                        Caches.boards[boardID].name = boardName_editTextView.text.toString()
                    }
                }
                textView.clearFocus()
                textView.hideSoftKeyboard()
                true
            } else false
        }

        deleteBoard_imageButton.setOnClickListener {
            MaterialConfirmDialog().apply {
                title = this@ViewBoardFragment.mainActivity.getString(R.string.deleteBoardQuestion)
                message = this@ViewBoardFragment.mainActivity.getString(R.string.deleteBoardDetails)
                onConfirm = View.OnClickListener {
                    this.dismiss()
                    Caches.deleteBoard(boardID)
                    finish()
                }
            }.show(mainActivity.supportFragmentManager, "MaterialConfirmDialog")
        }

        boardView.adapter = BoardAdapter(element.id)

        addList_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = element.id
                it.mainActivity.viewModel.boardPosition = false to boardView.boardAdapter.itemCount - 1

                replace(R.id.fragmentContainer, CreateListFragment.newInstance(), CREATE_LIST_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
            }.commit()
        }

        if (boardView.boardAdapter.itemCount > 0) {
            boardView.postDelayed(
                    {
                        viewModel.boardPosition.apply {
                            if (first) boardView.smoothScrollToPosition(second)
                        }
                    },
                    100L
            )
        }
    }

    override fun finish() {
        mainActivity.supportFragmentManager.popBackStack()
    }
}
