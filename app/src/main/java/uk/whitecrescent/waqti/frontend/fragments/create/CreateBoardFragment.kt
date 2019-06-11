@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_board.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.addAfterTextChangedListener
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.requestFocusAndShowSoftKeyboard

class CreateBoardFragment : WaqtiCreateFragment<Board>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_board, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpViews()
    }

    override fun setUpViews() {

        setUpButton()

        setUpAppBar()

    }

    private inline fun setUpAppBar() {
        boardName_editText.apply {
            requestFocusAndShowSoftKeyboard()
            mainActivity.hideableEditTextView = this
            addAfterTextChangedListener {
                if (it != null) {
                    addBoard_button.isVisible = !(it.isEmpty() || it.isBlank())
                }
            }
        }
    }

    private inline fun setUpButton() {
        addBoard_button.apply {
            isVisible = false
            setOnClickListener {
                Caches.boardLists.first().add(createElement()).update()
                finish()
            }
        }
    }

    override fun createElement(): Board {
        return Board(boardName_editText.text.toString())
    }

    override fun finish() {
        boardName_editText.hideSoftKeyboard()
        mainActivityViewModel.boardListPosition = true to mainActivityViewModel.boardListPosition.second + 1
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }

}
