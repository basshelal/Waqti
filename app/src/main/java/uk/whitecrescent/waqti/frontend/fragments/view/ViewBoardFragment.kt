package uk.whitecrescent.waqti.frontend.fragments.view

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.SpannableStringBuilder
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.color.colorChooser
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.board_options.view.*
import kotlinx.android.synthetic.main.fragment_board_view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.alsoIfNotNull
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.convertDpToPx
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.fadeIn
import uk.whitecrescent.waqti.fadeOut
import uk.whitecrescent.waqti.frontend.CREATE_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme.Companion.getAllColorSchemes
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.fragments.create.CreateListFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.horizontalFABOnScrollListener
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.shortSnackBar

class ViewBoardFragment : WaqtiViewFragment<Board>() {

    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = mainActivityVM.boardID

        if (mainActivityVM.boardAdapter == null ||
                mainActivityVM.boardAdapter?.boardID != boardID) {
            mainActivityVM.boardAdapter = BoardAdapter(boardID)
        }

        setUpViews(Caches.boards[boardID])
    }

    override fun setUpViews(element: Board) {
        setUpAppBar(element)

        boardView {
            if (mainActivityVM.settingsChanged) {
                invalidateBoard()
                mainActivityVM.settingsChanged = false
            }
        }

        doInBackground {
            boardView {
                adapter = mainActivityVM.boardAdapter
                if (boardAdapter.board.isEmpty()) {
                    emptyState_scrollView.isVisible = true
                    addList_floatingButton.customSize = convertDpToPx(85, mainActivity)
                }
                background = ColorScheme.getAllColorSchemes().random().light.toColorDrawable
                addOnScrollListener(this@ViewBoardFragment.addList_floatingButton.horizontalFABOnScrollListener)
            }

            addList_floatingButton {
                setOnClickListener {
                    @FragmentNavigation(from = VIEW_BOARD_FRAGMENT, to = CREATE_LIST_FRAGMENT)
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivityViewModel.boardID = element.id
                        it.mainActivityViewModel.boardPosition
                                .changeTo(false to boardView.boardAdapter.itemCount - 1)

                        it.clearFocusAndHideKeyboard()

                        replace(R.id.fragmentContainer, CreateListFragment(), CREATE_LIST_FRAGMENT)
                        addToBackStack(null)
                    }
                }
            }

            delete_imageView {
                alpha = 0F
                setOnDragListener { _, event ->
                    if (event.localState is DragEventLocalState) {
                        val draggingState = event.localState as DragEventLocalState
                        when (event.action) {
                            DragEvent.ACTION_DRAG_STARTED -> {
                                alpha = 1F
                                fadeIn(200)
                            }
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                (mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrateCompat(50)
                            }
                            DragEvent.ACTION_DROP -> {
                                ConfirmDialog().apply {
                                    title = this@ViewBoardFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                                    onConfirm = {
                                        val taskName = Caches.tasks[draggingState.taskID].name
                                        Caches.deleteTask(draggingState.taskID, draggingState.taskListID)
                                        this@ViewBoardFragment.boardView.boardAdapter
                                                .getListAdapter(draggingState.taskListID)?.apply {
                                                    notifyItemRemoved(taskListView
                                                            .findViewHolderForItemId(draggingState.taskID)
                                                            .adapterPosition)
                                                }
                                        mainActivity.appBar.shortSnackBar(getString(R.string.deletedTask)
                                                + " $taskName")
                                        this.dismiss()
                                    }
                                }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                            }
                            DragEvent.ACTION_DRAG_ENDED -> {
                                fadeOut(200)
                            }
                        }
                    }
                    true
                }
            }

            boardFragment_progressBar?.visibility = View.GONE
        }
    }

    private fun setUpAppBar(element: Board) {
        mainActivity.resetNavBarStatusBarColor()
        mainActivity.appBar {
            color = element.barColor
            elevation = DEFAULT_ELEVATION
            leftImageBack()
            editTextView {
                textColor = WaqtiColor.WAQTI_WHITE.toAndroidColor
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.boardNameHint)
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != element.name)
                            element.name = it.toString()
                    }
                }
                text = SpannableStringBuilder(element.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        textView.clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }
            rightImageDefault()
            /*R.id.changeBoardColor_menuItem -> {
                ColorPickerDialog().apply {
                    title = this@ViewBoardFragment.getString(R.string.pickBoardColor)
                    initialColor = Caches.boards[boardID].backgroundColor
                    onClick = {
                        this@ViewBoardFragment.boardView.background = it.toColorDrawable
                    }
                    onConfirm = {
                        Caches.boards[boardID].backgroundColor = it
                        dismiss()
                    }
                    onCancel = View.OnClickListener {
                        Caches.boards[boardID].backgroundColor = initialColor
                        this@ViewBoardFragment.boardView.background = initialColor.toColorDrawable
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "ColorPickerDialog")
                true
            }*/
            /*R.id.changeCardColor_menuItem -> {
                ColorPickerDialog().apply {
                    title = this@ViewBoardFragment.getString(R.string.pickCardColor)
                    initialColor = Caches.boards[boardID].cardColor
                    onClick = { color ->
                        this@ViewBoardFragment.boardView.allCards.forEach {
                            it.setCardBackgroundColor(color.toAndroidColor)
                        }
                    }
                    onConfirm = {
                        Caches.boards[boardID].cardColor = it
                        dismiss()
                    }
                    onCancel = View.OnClickListener {
                        Caches.boards[boardID].cardColor = initialColor
                        this@ViewBoardFragment.boardView.allCards.forEach {
                            it.setCardBackgroundColor(initialColor.toAndroidColor)
                        }
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "ColorPickerDialog")
                true
            }*/
            /*R.id.changeAppBarColor_menuItem -> {
                ColorPickerDialog().apply {
                    title = this@ViewBoardFragment.getString(R.string.pickAppBarColor)
                    initialColor = Caches.boards[boardID].barColor
                    onClick = { color ->
                        this@ViewBoardFragment.board_appBar.setBackgroundColor(color)
                        mainActivity.setNavigationBarColor(color)
                        mainActivity.setStatusBarColor(color)
                    }
                    onConfirm = {
                        Caches.boards[boardID].barColor = it
                        dismiss()
                    }
                    onCancel = View.OnClickListener {
                        Caches.boards[boardID].barColor = initialColor
                        this@ViewBoardFragment.board_appBar.setBackgroundColor(initialColor)
                        mainActivity.setNavigationBarColor(initialColor)
                        mainActivity.setStatusBarColor(initialColor)
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "ColorPickerDialog")
                true
            }*/
            /*R.id.changeBoardPhoto -> {
                PhotoPickerDialog().apply {
                    title = this@ViewBoardFragment.getString(R.string.pickBoardBackground)
                    initialPhoto = Caches.boards[boardID].backgroundPhoto
                    onClick = { photo ->

                    }
                    __onClick = {
                        this@ViewBoardFragment.boardView.background = it
                    }
                    __onConfirm = {
                        this@ViewBoardFragment.boardView.background = it
                    }
                    onConfirm = {

                        dismiss()
                    }
                    onCancel = View.OnClickListener {

                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "PhotoPickerDialog")
                true
            }*/
            /*R.id.boardAppearance_menuItem -> {
                // here we show a Dialog which allows user to pick which
                // customization option to change, when picked it will open a dialog
                // for that one
                shortSnackBar("Not yet implemented!")
                true
            }*/
        }
        ColorScheme.getAllColorSchemes().random().also {
            mainActivity.setAppBarColorScheme(it)
            boardView.setEdgeEffectColor(it.dark)
        }
    }

    override fun onResume() {
        super.onResume()

        LayoutInflater.from(context).inflate(R.layout.board_options,
                mainActivity.drawerLayout, true)

        mainActivity.appBar {
            rightImageView.setOnClickListener {
                mainActivity.drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        mainActivity.drawerLayout.boardOptions_navigationView {
            appBarColor_boardOption {
                setOnClickListener {
                    com.afollestad.materialdialogs.MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        cornerRadius(literalDp = 8F)
                        title(text = "App Bar Color")
                        colorChooser(colors = getAllColorSchemes(ColorScheme.Level.FIVE_HUNDRED)
                                .map { it.main.toAndroidColor }
                                .toIntArray(),
                                subColors = ColorScheme.Color.values().map { color ->
                                    ColorScheme.Level.values().map { level ->
                                        ColorScheme.getColorScheme(ColorScheme.ColorLevel(color,
                                                level)).main.toAndroidColor
                                    }.toIntArray()
                                }.toTypedArray())
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            cardColor_boardOption { setOnClickListener { } }
            boardColor_boardOption { setOnClickListener { } }
            boardImage_boardOption { setOnClickListener { } }
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

    override fun onStop() {
        super.onStop()

        mainActivity.drawerLayout {
            boardOptions_navigationView.alsoIfNotNull {
                closeDrawer(it)
                removeView(it)
            }
        }
    }

    override fun finish() {
        @FragmentNavigation(from = VIEW_BOARD_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }
}
