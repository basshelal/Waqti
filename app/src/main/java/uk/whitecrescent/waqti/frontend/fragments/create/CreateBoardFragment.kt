@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_board.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
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
        mainActivity.appBar {
            color = WaqtiColor.WHITE
            elevation = 0F
            leftImageView.isVisible = false
            editTextView {
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.boardNameHint)
                text = SpannableStringBuilder("")
                requestFocusAndShowSoftKeyboard()
                addAfterTextChangedListener {
                    if (it != null) {
                        addBoard_button.isVisible = !(it.isEmpty() || it.isBlank())
                    }
                }
            }
            rightImageView.isVisible = false
        }
    }

    private inline fun setUpButton() {
        addBoard_button.apply {
            isVisible = false
            setOnClickListener {
                Caches.boardList.add(createElement()).update()
                finish()
            }
        }
    }

    override fun createElement(): Board {
        return Board(mainActivity.appBar.editTextView.text.toString())
    }

    override fun finish() {
        mainActivityVM.boardListPosition
                .changeTo(true to mainActivityVM.boardListPosition.position + 1)
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }

}
