@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.color.colorChooser
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.board_options.view.*
import kotlinx.android.synthetic.main.container_board_view.view.*
import kotlinx.android.synthetic.main.fragment_board_view.*
import org.jetbrains.anko.backgroundColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.extensions.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.extensions.commitTransaction
import uk.whitecrescent.waqti.extensions.doInBackground
import uk.whitecrescent.waqti.extensions.getViewModel
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.mainActivity
import uk.whitecrescent.waqti.extensions.shortSnackBar
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.BackgroundType
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.toColor
import uk.whitecrescent.waqti.frontend.customview.AppBar.Companion.DEFAULT_ELEVATION
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.PhotoPickerDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardView
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel

class ViewBoardFragment : WaqtiViewFragment() {

    private var boardID: ID = 0L
    private lateinit var viewModel: ViewBoardFragmentViewModel
    private lateinit var board: Board

    private inline val boardView: BoardView
        get() = boardViewContainer.boardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardID = mainActivityVM.boardID
        board = Caches.boards[boardID]

        viewModel = getViewModel()

        if (mainActivityVM.boardAdapter == null ||
                mainActivityVM.boardAdapter?.boardID != boardID) {
            mainActivityVM.boardAdapter = BoardAdapter(boardID)
        }

        setUpViews()
    }

    override fun setUpViews() {
        doInBackground {
            setUpAppBar()
        }
    }

    override fun setUpAppBar() {
        this.setColorScheme(board.barColor.colorScheme)
        mainActivity.appBar {
            elevation = DEFAULT_ELEVATION
            leftImageBack()
            editTextView {
                isEditable = true
                hint = getString(R.string.boardNameHint)
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != board.name)
                            board.name = it.toString()
                    }
                }
                textChangedListener = { update() }
                text = SpannableStringBuilder(board.name)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }
            rightImageView.isVisible = true
            rightImage = R.drawable.overflow_icon
        }
        mainActivity.setColorScheme(board.barColor.colorScheme)
    }

    override fun onStart() {
        super.onStart()

        createOptionsMenu()
    }

    override fun onStop() {
        super.onStop()

        destroyOptionsMenu()
    }

    override fun finish() {
        @FragmentNavigation(from = VIEW_BOARD_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }

    private inline fun createOptionsMenu() {

        LayoutInflater.from(context).inflate(R.layout.board_options,
                mainActivity.drawerLayout, true)

        mainActivity.appBar {
            rightImageView.setOnClickListener {
                mainActivity.drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        mainActivity.drawerLayout.boardOptions_navigationView {
            boardOptions_scrollView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = mainActivity.appBar.height
            }
            setBackgroundColor(board.barColor.toAndroidColor)
            appBarColor_boardOption {
                setOnClickListener {
                    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(text = "App Bar Color")
                        setPeekHeight(Int.MAX_VALUE)
                        positiveButton(text = "Confirm")
                        colorChooser(colors = ColorScheme.materialDialogsMainColors(),
                                subColors = ColorScheme.materialDialogsAllColors(),
                                initialSelection = board.barColor.toAndroidColor,
                                changeActionButtonsColor = true,
                                waitForPositiveButton = false,
                                selection = { dialog, colorInt ->
                                    val colorScheme = colorInt.toColor.colorScheme
                                    if (mainActivity.preferences.changeNavBarColor)
                                        dialog.window?.navigationBarColor = colorScheme.main.toAndroidColor
                                    this@ViewBoardFragment.setColorScheme(colorScheme)
                                    board.barColor = colorScheme.main
                                }
                        )
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            listHeaderColor_boardOption {
                setOnClickListener {
                    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(text = "List Header Color")
                        setPeekHeight(Int.MAX_VALUE)
                        positiveButton(text = "Confirm")
                        colorChooser(colors = ColorScheme.materialDialogsMainColors(),
                                subColors = ColorScheme.materialDialogsAllColors(),
                                initialSelection = board.listColor.toAndroidColor,
                                changeActionButtonsColor = true,
                                waitForPositiveButton = false,
                                selection = { _, colorInt ->
                                    val colorScheme = colorInt.toColor.colorScheme
                                    this@ViewBoardFragment.boardView
                                            .boardAdapter?.setHeadersColorScheme(colorScheme)
                                    board.listColor = colorScheme.main
                                }
                        )
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            cardColor_boardOption {
                setOnClickListener {
                    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(text = "Card Color")
                        setPeekHeight(Int.MAX_VALUE)
                        positiveButton(text = "Confirm")
                        colorChooser(colors = ColorScheme.materialDialogsMainColors(),
                                subColors = ColorScheme.materialDialogsAllColors(),
                                initialSelection = board.cardColor.toAndroidColor,
                                changeActionButtonsColor = true,
                                waitForPositiveButton = false,
                                selection = { _, colorInt ->
                                    val colorScheme = colorInt.toColor.colorScheme
                                    this@ViewBoardFragment.boardView
                                            .boardAdapter?.setListsColorScheme(colorScheme)
                                    board.cardColor = colorScheme.main
                                }
                        )
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            boardColor_boardOption {
                setOnClickListener {
                    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(text = "Background Color")
                        setPeekHeight(Int.MAX_VALUE)
                        positiveButton(text = "Confirm")
                        colorChooser(colors = ColorScheme.materialDialogsMainColors(),
                                subColors = ColorScheme.materialDialogsAllColors(),
                                initialSelection = board.backgroundColor.toAndroidColor,
                                changeActionButtonsColor = true,
                                waitForPositiveButton = false,
                                selection = { _, colorInt ->
                                    val waqtiColor = colorInt.toColor
                                    //setBackgroundColor(waqtiColor)
                                    board.backgroundColor = waqtiColor
                                    board.backgroundType = BackgroundType.COLOR
                                    /*if (this@ViewBoardFragment.emptyState_scrollView.isVisible) {
                                        this@ViewBoardFragment.emptyTitle_textView.textColor = waqtiColor.colorScheme.text.toAndroidColor
                                        this@ViewBoardFragment.emptySubtitle_textView.textColor = waqtiColor.colorScheme.text.toAndroidColor
                                    }*/
                                }
                        )
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            boardImage_boardOption {
                setOnClickListener {
                    val photoPicker = PhotoPickerDialog().apply {
                        onClick = {}
                        onCancel = {
                            //setBackground()
                            dismiss()
                        }
                        onConfirm = {
                            board.backgroundPhoto = it
                            board.backgroundType = BackgroundType.UNSPLASH_PHOTO
                            //setBackgroundImage(it)
                            dismiss()
                        }
                    }
                    mainActivity.supportFragmentManager.commitTransaction {
                        add(R.id.fragmentContainer, photoPicker, "PhotoPicker")
                        addToBackStack(null)
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            boardOptions_divider {
                backgroundColor = board.barColor.colorScheme.text.toAndroidColor
            }
            deleteBoard_boardOption {
                setOnClickListener {
                    ConfirmDialog().apply {
                        title = this@ViewBoardFragment.mainActivity.getString(R.string.deleteBoardQuestion)
                        message = this@ViewBoardFragment.mainActivity.getString(R.string.deleteBoardDetails)
                        onConfirm = {
                            val boardName = Caches.boards[boardID].name
                            dismiss()
                            Caches.deleteBoard(boardID)
                            mainActivity.appBar.shortSnackBar(getString(R.string.deletedBoard)
                                    + " $boardName")
                            finish()
                        }
                    }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
        }
    }

    private inline fun destroyOptionsMenu() {
        mainActivity.drawerLayout {
            boardOptions_navigationView?.also {
                closeDrawer(it)
                removeView(it)
            }
        }
    }

    private inline fun setColorScheme(colorScheme: ColorScheme) {
        mainActivity.setColorScheme(colorScheme)
        mainActivity.drawerLayout.boardOptions_navigationView {
            setBackgroundColor(colorScheme.main.toAndroidColor)
            boardOptions_divider {
                backgroundColor = colorScheme.text.toAndroidColor
            }
        }
        boardViewContainer.setColorScheme(colorScheme)
    }

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = VIEW_BOARD_LIST_FRAGMENT, to = VIEW_BOARD_FRAGMENT)
                replace(R.id.fragmentContainer, ViewBoardFragment(), VIEW_BOARD_FRAGMENT)
                addToBackStack(null)
            }
        }
    }
}

class ViewBoardFragmentViewModel : WaqtiViewFragmentViewModel()
