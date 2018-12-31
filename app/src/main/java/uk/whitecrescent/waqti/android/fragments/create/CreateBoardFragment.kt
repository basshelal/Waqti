package uk.whitecrescent.waqti.android.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_board.*
import uk.whitecrescent.waqti.BuildConfig
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.openKeyboard
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches

class CreateBoardFragment : WaqtiCreateFragment<Board>() {

    companion object {
        fun newInstance() = CreateBoardFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_board, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpViews()
    }

    override fun setUpViews() {
        if (!BuildConfig.DEBUG) dev_addBoard_button.visibility = View.GONE

        mainActivity.supportActionBar?.title = "New Board"

        boardName_editText.openKeyboard()

        boardName_editText.addAfterTextChangedListener {
            if (it != null) {
                addBoard_button.isEnabled = !(it.isEmpty() || it.isBlank())
            }
        }

        addBoard_button.isEnabled = false
        addBoard_button.setOnClickListener {
            Caches.boardLists.first().add(createElement()).update()
            finish()
        }

        dev_addBoard_button.setOnClickListener {
            Caches.boardLists.first().add(Board("Dev Board")).update()
            finish()
        }
    }

    override fun createElement(): Board {
        return Board(boardName_editText.text.toString())
    }

    override fun finish() {
        boardName_editText.hideSoftKeyboard()
        viewModel.boardListPosition = true to viewModel.boardListPosition.second + 1
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }

}
