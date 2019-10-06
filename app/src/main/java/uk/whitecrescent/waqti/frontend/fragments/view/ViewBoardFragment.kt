@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.view

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.text.SpannableStringBuilder
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.color.colorChooser
import com.bumptech.glide.Glide
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.board_options.view.*
import kotlinx.android.synthetic.main.fragment_board_view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.convertDpToPx
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.fadeIn
import uk.whitecrescent.waqti.fadeOut
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.BackgroundType
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.appearance.toColor
import uk.whitecrescent.waqti.frontend.customview.AppBar.Companion.DEFAULT_ELEVATION
import uk.whitecrescent.waqti.frontend.customview.DragView
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.PhotoPickerDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.fragments.create.CreateListFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.horizontalFABOnScrollListener
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.logE
import uk.whitecrescent.waqti.longSnackBar
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.setColorScheme
import uk.whitecrescent.waqti.setEdgeEffectColor
import uk.whitecrescent.waqti.shortSnackBar
import kotlin.math.roundToInt

class ViewBoardFragment : WaqtiViewFragment() {

    private var boardID: ID = 0L
    private lateinit var viewModel: ViewBoardFragmentViewModel
    private lateinit var board: Board

    private var draggingVHID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = mainActivityVM.boardID
        board = Caches.boards[boardID]

        viewModel = getViewModel()

        if (mainActivityVM.boardAdapter == null ||
                mainActivityVM.boardAdapter?.boardID != boardID) {
            mainActivityVM.boardAdapter = BoardAdapter(boardID)
        }

        setUpViews()

        task_dragView.setItemViewId(R.layout.task_card)

        task_dragView.updateLayoutParams {
            width = 700
            height = 150
        }

        task_dragView.onStateChanged = { dragState: DragView.DragState ->
            when (dragState) {
                DragView.DragState.IDLE -> {
                    task_dragView.backgroundColor = Color.RED
                }
                DragView.DragState.DRAGGING -> {
                    task_dragView.backgroundColor = Color.CYAN
                    task_dragView.itemView!!.backgroundColor = Color.LTGRAY
                }
                DragView.DragState.SETTLING -> {
                    task_dragView.backgroundColor = Color.RED
                }
            }
        }

        task_dragView.setOnLongClickListener {
            task_dragView.startDrag()
            true
        }

        task_dragView.dragListener = object : DragView.SimpleDragListener() {
            override fun onStartDrag(dragView: DragView) {
                this@ViewBoardFragment.mainActivity.appBar.shortSnackBar("Start Drag Task $draggingVHID")
                logE("Start Drag Task @ $draggingVHID")
            }

            override fun onReleaseDrag(dragView: DragView, touchPoint: PointF) {
                this@ViewBoardFragment.mainActivity.appBar.shortSnackBar("Release Drag Task $draggingVHID")
                logE("Release Drag Task @ $draggingVHID")

                this@ViewBoardFragment.boardView.boardAdapter?.taskListAdapters?.find {
                    it.allViewHolders.firstOrNull { it.taskID == draggingVHID } != null
                }.also {
                    it?.taskListView?.findViewHolderForItemId(draggingVHID)?.itemView
                            ?.backgroundColor = Color.MAGENTA
                }
            }

            override fun onEndDrag(dragView: DragView) {
                this@ViewBoardFragment.mainActivity.appBar.shortSnackBar("End Drag Task $draggingVHID")
                logE("End Drag Task @ $draggingVHID")
            }
        }

    }

    override fun setUpViews() {
        setBackground()
        boardView {
            if (mainActivityVM.settingsChanged) {
                invalidateBoard()
                mainActivityVM.settingsChanged = false
            }
            adapter = mainActivityVM.boardAdapter


            boardAdapter?.onStartDragTask = {
                draggingVHID = it.itemId
                task_dragView.find<ProgressBar>(R.id.taskCard_progressBar).isVisible = false
                task_dragView.find<TextView>(R.id.task_textView).apply {
                    isVisible = true
                    text = it.textView.text
                }

                task_dragView.startDragFromView(it.itemView)
            }


            if (boardAdapter?.board?.isEmpty() == true) {
                emptyTitle_textView.textColor = board.backgroundColor.colorScheme.text.toAndroidColor
                emptySubtitle_textView.textColor = board.backgroundColor.colorScheme.text.toAndroidColor
                emptyState_scrollView.isVisible = true
                addList_floatingButton.customSize = convertDpToPx(85, mainActivity)
            }
            addOnScrollListener(this@ViewBoardFragment.addList_floatingButton.horizontalFABOnScrollListener)
        }
        doInBackground {
            setUpAppBar()
            addList_floatingButton {
                setOnClickListener {
                    mainActivityViewModel.boardID = board.id
                    mainActivityViewModel.boardPosition
                            .changeTo(false to (boardView.boardAdapter?.itemCount ?: 0 - 1))

                    CreateListFragment.show(mainActivity)
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
                                (mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator?)
                                        ?.vibrateCompat(50)
                            }
                            DragEvent.ACTION_DROP -> {
                                ConfirmDialog().apply {
                                    title = this@ViewBoardFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                                    onConfirm = {
                                        val taskName = Caches.tasks[draggingState.taskID].name
                                        Caches.deleteTask(draggingState.taskID, draggingState.taskListID)
                                        this@ViewBoardFragment.boardView.boardAdapter
                                                ?.getListAdapter(draggingState.taskListID)?.apply {
                                                    taskListView?.findViewHolderForItemId(draggingState.taskID)
                                                            ?.adapterPosition?.also {
                                                        notifyItemRemoved(it)
                                                    }
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
                setOnEditorActionListener { textView, actionId, _ ->
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
                                    setBackgroundColor(waqtiColor)
                                    board.backgroundColor = waqtiColor
                                    board.backgroundType = BackgroundType.COLOR
                                    if (this@ViewBoardFragment.emptyState_scrollView.isVisible) {
                                        this@ViewBoardFragment.emptyTitle_textView.textColor = waqtiColor.colorScheme.text.toAndroidColor
                                        this@ViewBoardFragment.emptySubtitle_textView.textColor = waqtiColor.colorScheme.text.toAndroidColor
                                    }
                                }
                        )
                    }
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
            }
            boardImage_boardOption {
                setOnClickListener {
                    val photoPicker = PhotoPickerDialog().apply {
                        onClick = {
                            setBackgroundImage(it)
                        }
                        onCancel = {
                            setBackground()
                            dismiss()
                        }
                        onConfirm = {
                            board.backgroundPhoto = it
                            board.backgroundType = BackgroundType.UNSPLASH_PHOTO
                            dismiss()
                        }
                    }
                    mainActivity.supportFragmentManager.commitTransaction {
                        add(R.id.fragmentContainer, photoPicker, "PhotoPicker")
                        addToBackStack(null)
                    }
                    mainActivity.appBar.longSnackBar("Long click to View, touch to select ")
                    mainActivity.drawerLayout.closeDrawer(this@boardOptions_navigationView)
                }
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

    private inline fun setBackground() {
        doInBackground {
            when (board.backgroundType) {
                BackgroundType.COLOR -> {
                    setBackgroundColor(board.backgroundColor)
                }
                BackgroundType.UNSPLASH_PHOTO -> {
                    setBackgroundImage(board.backgroundPhoto)
                }
            }
        }
    }

    private inline fun setBackgroundImage(photo: UnsplashPhoto) {
        background_imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = MATCH_PARENT
        }
        Glide.with(this)
                .load(Uri.parse(photo.urls.regular))
                .centerCrop()
                .into(background_imageView)

    }

    private inline fun setBackgroundColor(waqtiColor: WaqtiColor) {
        background_imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = MATCH_PARENT
        }

        background_imageView.setImageDrawable(null)
        background_imageView.background = waqtiColor.toColorDrawable
        boardView.scrollBarColor = waqtiColor.colorScheme.text
    }

    private inline fun setColorScheme(colorScheme: ColorScheme) {
        mainActivity.setColorScheme(colorScheme)
        mainActivity.drawerLayout.boardOptions_navigationView {
            setBackgroundColor(colorScheme.main.toAndroidColor)
        }
        addList_floatingButton.setColorScheme(colorScheme)
        boardView.setEdgeEffectColor(colorScheme.dark)
    }

    @ForLater
    private inline fun parallaxImage(photo: UnsplashPhoto) {
        doInBackground {
            /* TODO
              * The scrollBy will differ based on how wide the image is
              * the wider the image the smaller the number.
              * Or the number of lists??
              * Actually I think its based on number of lists not image width!
              * What if it's based on a relation between both?
              */

            background_imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                width = WRAP_CONTENT
            }

            /*
            * Log from Glide:
            *
            * Glide treats LayoutParams.WRAP_CONTENT as a request for an image the size of this
            * device's screen dimensions. If you want to load the original image and are ok with
            * the corresponding memory cost and OOMs (depending on the input size), use
            * .override(Target.SIZE_ORIGINAL). Otherwise, use LayoutParams.MATCH_PARENT, set
            * layout_width and layout_height to fixed dimension, or use .override() with fixed
            * dimensions.
            *
            * */

            Glide.with(this)
                    .load(Uri.parse(photo.urls.regular))
                    .centerCrop()
                    .into(background_imageView)

            // below is only if we want parallax!
            boardView.boardAdapter?.apply {
                mainActivity.appBar.shortSnackBar("$horizontalScrollOffset")
                onScrolled = { dx, _ ->
                    // below prevents over-scrolling leftwards which shows white space
                    if (horizontalScrollOffset > 0)
                        background_imageView.scrollBy((dx * 0.25).roundToInt(), 0)
                    mainActivity.appBar.shortSnackBar("$horizontalScrollOffset")
                }
            }
        }
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
