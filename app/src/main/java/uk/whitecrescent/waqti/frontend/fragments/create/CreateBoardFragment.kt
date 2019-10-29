@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_board.*
import org.jetbrains.anko.backgroundColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.extensions.commitTransaction
import uk.whitecrescent.waqti.extensions.getViewModel
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.requestFocusAndShowKeyboard
import uk.whitecrescent.waqti.extensions.smoothScrollToEnd
import uk.whitecrescent.waqti.frontend.CREATE_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.BoardAppearance
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragmentViewModel

class CreateBoardFragment : WaqtiCreateFragment<Board>() {

    override lateinit var viewModel: CreateBoardFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_board, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
                resetTextColor()
                isEditable = true
                hint = getString(R.string.boardNameHint)
                textChangedListener = {
                    if (it != null) {
                        addBoard_button.isVisible = !(it.isEmpty() || it.isBlank())
                    }
                }
                text = SpannableStringBuilder("")
                requestFocusAndShowKeyboard()
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
        return Board(mainActivity.appBar.editTextView.text.toString()).apply {
            if (mainActivity.isDarkTheme) appearance = BoardAppearance.DEFAULT_DARK
        }
    }

    override fun finish() {
        mainActivityVM.onInflateBoardListView = {
            it.smoothScrollToEnd()
        }
        @FragmentNavigation(from = CREATE_BOARD_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = VIEW_BOARD_LIST_FRAGMENT, to = CREATE_BOARD_FRAGMENT)
                replace(R.id.fragmentContainer, CreateBoardFragment(), CREATE_BOARD_FRAGMENT)
                addToBackStack(null)
            }
        }
    }

}

class CreateBoardFragmentViewModel : WaqtiCreateFragmentViewModel<Board>() {

    override fun createElement(fromFragment: Board): Board {
        TODO()
    }

}
