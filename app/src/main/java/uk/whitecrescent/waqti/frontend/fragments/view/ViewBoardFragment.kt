@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.view

import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.contains
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.color.colorChooser
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.board_options.view.*
import kotlinx.android.synthetic.main.fragment_board_view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.vibrator
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.extensions.D
import uk.whitecrescent.waqti.extensions.F
import uk.whitecrescent.waqti.extensions.L
import uk.whitecrescent.waqti.extensions.addOnScrollListener
import uk.whitecrescent.waqti.extensions.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.extensions.commitTransaction
import uk.whitecrescent.waqti.extensions.convertDpToPx
import uk.whitecrescent.waqti.extensions.doInBackground
import uk.whitecrescent.waqti.extensions.fadeIn
import uk.whitecrescent.waqti.extensions.fadeOut
import uk.whitecrescent.waqti.extensions.getViewModel
import uk.whitecrescent.waqti.extensions.globalVisibleRectF
import uk.whitecrescent.waqti.extensions.horizontalFABOnScrollListener
import uk.whitecrescent.waqti.extensions.horizontalPercentInverted
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.logE
import uk.whitecrescent.waqti.extensions.mainActivity
import uk.whitecrescent.waqti.extensions.mainActivityViewModel
import uk.whitecrescent.waqti.extensions.setColorScheme
import uk.whitecrescent.waqti.extensions.setEdgeEffectColor
import uk.whitecrescent.waqti.extensions.shortSnackBar
import uk.whitecrescent.waqti.extensions.verticalPercent
import uk.whitecrescent.waqti.extensions.verticalPercentInverted
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
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.PhotoPickerDialog
import uk.whitecrescent.waqti.frontend.customview.drag.ObservableDragBehavior
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardViewHolder
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskListAdapter
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskListView
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskViewHolder
import uk.whitecrescent.waqti.frontend.fragments.create.CreateListFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel
import uk.whitecrescent.waqti.frontend.vibrateCompat
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class ViewBoardFragment : WaqtiViewFragment() {

    private var boardID: ID = 0L
    private lateinit var viewModel: ViewBoardFragmentViewModel
    private lateinit var board: Board

    private var dragTaskID: ID = 0L
    private var dragListID: ID = 0L

    private inline val realScreenWidth get() = mainActivity.realScreenWidth
    private inline val realScreenHeight get() = mainActivity.realScreenHeight

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
        setBackground()

        boardView {
            if (mainActivityVM.settingsChanged) {
                invalidateBoard()
                mainActivityVM.settingsChanged = false
            }
            adapter = mainActivityVM.boardAdapter

            boardAdapter?.onStartDragTask = {
                dragTaskID = it.itemId
                task_dragShadow updateToMatch it.itemView
                task_dragShadow.updateLayoutParams {
                    width = WRAP_CONTENT
                    height = WRAP_CONTENT
                }
                this@ViewBoardFragment.task_dragShadow.dragBehavior.startDragFromView(it.itemView)
            }

            boardAdapter?.onInterceptTouchEvent = { vh, event ->
                logE("onInterceptTouchEvent in ViewBoardFragment with VH and MotionEvent")
            }

            boardAdapter?.onTouchEvent = { vh, event ->
                logE("onTouchEvent in ViewBoardFragment with VH and MotionEvent")
                task_dragShadow?.dispatchTouchEvent(event)
            }

            boardAdapter?.onStartDragList = {
                dragListID = it.itemId
                list_dragShadow updateToMatch it.itemView
                this@ViewBoardFragment.list_dragShadow.dragBehavior.startDragFromView(it.header)
            }

            if (boardAdapter?.board?.isEmpty() == true) {
                emptyTitle_textView.textColor = board.backgroundColor.colorScheme.text.toAndroidColor
                emptySubtitle_textView.textColor = board.backgroundColor.colorScheme.text.toAndroidColor
                emptyState_scrollView.isVisible = true
                addList_floatingButton.customSize = (mainActivity convertDpToPx 85).roundToInt()
            }
            addOnScrollListener(this@ViewBoardFragment.addList_floatingButton.horizontalFABOnScrollListener)
            //parallaxImage(board.backgroundPhoto)
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
                                mainActivity.vibrator.vibrateCompat(50)
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

            setUpTaskDrag()
            setUpListDrag()

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

    private inline fun setUpTaskDrag() {

        task_dragShadow {

            dragBehavior.dragListener = object : ObservableDragBehavior.SimpleDragListener() {

                // The VH we are currently dragging
                private var draggingViewHolder: TaskViewHolder? = null

                // The VH we are currently over
                private var currentViewHolder: TaskViewHolder? = null

                private lateinit var taskListView: TaskListView

                private val scrollUpBounds: RectF
                    get() = taskListView.globalVisibleRectF.apply {
                        bottom = top + (taskListView.height.F * 0.05F)
                        top = 0F
                    }
                private val scrollDownBounds: RectF
                    get() = taskListView.globalVisibleRectF.apply {
                        top = bottom - (taskListView.height.F * 0.05F)
                        bottom = realScreenHeight.F
                    }
                private val scrollLeftBounds: RectF
                    get() = taskListView.globalVisibleRectF.apply {
                        right = left
                        left = 0F
                    }
                private val scrollRightBounds: RectF
                    get() = taskListView.globalVisibleRectF.apply {
                        left = right
                        right = realScreenWidth.F
                    }

                private val currentTouchPoint = PointF()

                private val frameRate = mainActivity.millisPerFrame.L

                private val scrollByPixels = frameRate.F / 1.5F

                private val observable =
                        Observable.interval(
                                frameRate,
                                TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.computation())

                private lateinit var disposable: Disposable

                override fun onStartDrag(dragView: View) {

                    draggingViewHolder = findViewHolder(dragTaskID)

                    //draggingViewHolder?.itemView?.alpha = 0F
                    draggingViewHolder?.itemView?.setBackgroundColor(Color.RED)

                    taskListView = boardView.boardAdapter!!.getListAdapter(draggingViewHolder!!.taskListID)!!.taskListView!!

                    currentTouchPoint.set(dragBehavior.initialTouchPoint)

                    disposable = observable.subscribe {
                        if (!currentTouchPoint.equals(0F, 0F)) {
                            // updateViewHolders(currentTouchPoint)
                            // checkForScroll(currentTouchPoint)
                        }
                    }

                    // TODO: 04-Nov-19 When dragging a ViewHolder that will be touched by a "notify" method call,
                    //  the drag we have is let go for some reason

                    // TODO: 28-Jan-20 When a "notify" method is called the drag is released
                    //  because many views including the one that is receiving the drag event is
                    //  being detached from the TaskListView, see RecyclerView line 6165, a
                    //  detached view will always lose any of its previous interactions as is
                    //  showin in ViewGroup.removeDetachedView at line 5690.
                    //  The question that remains is *why* the view is being detached, the only
                    //  info I can gather is that the Recycler is determining this in
                    //  RecyclerView.Recycler.tryGetViewHolderForPositionByDeadline in line 6140.
                    //  How this relates to our notify methods I still don't know, they may not
                    //  be directly related and instead the detachment is simply a side effect of
                    //  the notify rather than a direct result of it, meaning that after the
                    //  notify is done (which calls a requestLayout) the new layout pass
                    //  determines it needs to detach views including the one receiving our
                    //  dragging view and then later it will be reattached. This all makes sense,
                    //  but how do we counter this or solve it???

                    /* Possible solution?
                     * All TaskCardViews receive their touchEvents as normal UNTIL a request for
                     * start dragging is sent, in which case the events will NEVER deal with the
                     * TaskCardView and instead deal straight with the DragShadow, this means
                     * that no matter what happens to the original TaskCardView (ie, it gets
                     * detached from its parent, the TaskListView), the DragShadow will still
                     * receive all events and no ACTION_CANCEL will be sent.
                     *
                     * How in the hell can we go about this though?? Is there a way for events to
                     * change their target? Or for them to go to one View then after a while
                     * never go to that View and go to another? This is tricky and needs testing
                     * and experimenting, I haven't found much documentation on this online but I
                     * will try to look again.
                     */

                    /*
                     * Possible solution?
                     * Look into using TouchDelegate! It is able to pass events from one view to
                     * another but I'm not sure if it can allow it to not delegate until we tell
                     * it, likely we can though, something like:
                     * in TaskCardView:
                     * override fun onTouchEvent(){
                     *     return if(!delegated) super.onTouchEvent()
                     *         else delegateView.onTouchEvent()
                     * }
                     * I don't know, it's a maybe but we should definitely look into
                     * TouchDelegate as it seems to be of some potential value for us
                     */

                    /*
                     * Good solution?
                     * onInterceptTouchEvent of TaskListView, make the shadow be in the place of
                     * the touched position, make sure it receives all further touch Events and
                     * NOT the TaskCardView, even if the shadow is invisible. Then in the
                     * shadow's onTouch, let it call the onTouch of the TaskCardView its on top
                     * of UNTIL a drag is requested. This way all of the events from the very
                     * beginning are going to the shadow and the Card is receiving the events (in
                     * addition to the shadow) only when a drag is not happening, thus clicks and
                     * animations will still work properly.
                     */

                    postDelayed(2000) {
                        taskListView.listAdapter?.notifyItemRemoved(1)
                        // taskListView.dispatchTouchEvent(obtainTouchEvent(MotionEvent.ACTION_CANCEL, 0, 0))
                        // task_dragShadow?.dispatchTouchEvent(obtainTouchEvent(MotionEvent.ACTION_MOVE, 0, 0))
                    }

                }

                override fun onReleaseDrag(dragView: View, touchPoint: PointF) {
                }

                override fun onEndDrag(dragView: View) {

                    draggingViewHolder?.itemView?.alpha = 1F

                    currentViewHolder = null

                    disposable.dispose()
                }

                override fun onUpdateLocation(dragView: View, touchPoint: PointF) {
                    /** check [TaskListAdapter.onDrag]*/
                    currentTouchPoint.set(touchPoint)
                }

                override fun onDragStateChanged(dragView: View, newState: ObservableDragBehavior.DragState) {
                    when (newState) {
                        ObservableDragBehavior.DragState.IDLE -> {
                            task_dragShadow.isVisible = false
                        }
                        ObservableDragBehavior.DragState.DRAGGING -> {
                            task_dragShadow.isVisible = true
                            task_dragShadow.alpha = 0.8F
                        }
                        ObservableDragBehavior.DragState.SETTLING -> {
                            task_dragShadow.alpha = 1F
                        }
                    }
                }

                private inline fun updateViewHolders(touchPoint: PointF) {
                    if (touchPoint in scrollUpBounds) {
                        currentViewHolder = taskListView.findFirstVisibleViewHolder() as? TaskViewHolder
                        swapTaskViewHolders(draggingViewHolder, currentViewHolder)
                    } else if (touchPoint in scrollDownBounds) {
                        currentViewHolder = taskListView.findLastVisibleViewHolder() as? TaskViewHolder
                        swapTaskViewHolders(draggingViewHolder, currentViewHolder)
                    } else if (currentViewHolder != findViewHolderUnder(touchPoint)) {
                        currentViewHolder = findViewHolderUnder(touchPoint)
                        swapTaskViewHolders(draggingViewHolder, currentViewHolder)
                    }
                }

                private inline fun checkForScroll(touchPoint: PointF) {
                    scrollVertically(touchPoint)
                    scrollHorizontally(touchPoint)
                }

                private inline fun scrollVertically(touchPoint: PointF) {
                    if (taskListView.linearLayoutManager?.canScrollVertically() == true) {

                        if (touchPoint in scrollUpBounds &&
                                taskListView.verticalScrollOffset > 0) {
                            val percent = scrollUpBounds.verticalPercentInverted(touchPoint).roundToInt()
                            val multiplier = (percent.F / 100F) + 1F
                            val scrollAmount = (-scrollByPixels * multiplier).roundToInt()

                            taskListView.scrollBy(0, scrollAmount)

                            return
                        }
                        if (touchPoint in scrollDownBounds &&
                                taskListView.verticalScrollOffset < taskListView.maxVerticalScroll) {
                            val percent = scrollDownBounds.verticalPercent(touchPoint).roundToInt()
                            val multiplier = (percent.F / 100F) + 1F
                            val scrollAmount = (scrollByPixels * multiplier).roundToInt()

                            taskListView.scrollBy(0, scrollAmount)

                            return
                        }
                    }
                }

                private inline fun scrollHorizontally(touchPoint: PointF) {
                    if (boardView.linearLayoutManager?.canScrollHorizontally() == true) {

                        if (touchPoint in scrollLeftBounds &&
                                boardView.horizontalScrollOffset > 0) {
                            val percent = scrollLeftBounds.horizontalPercentInverted(touchPoint).roundToInt()
                            val multiplier = (percent.F / 100F) + 1F
                            val scrollAmount = (-scrollByPixels * multiplier).roundToInt()

                            boardView.scrollBy(scrollAmount, 0)

                            return
                        }
                        if (touchPoint in scrollRightBounds &&
                                boardView.horizontalScrollOffset < boardView.maxHorizontalScroll) {
                            val percent = scrollRightBounds.verticalPercent(touchPoint).roundToInt()
                            val multiplier = (percent.F / 100F) + 1F
                            val scrollAmount = (scrollByPixels * multiplier).roundToInt()

                            boardView.scrollBy(scrollAmount, 0)

                            return
                        }
                    }
                }

                private inline fun findViewHolder(id: ID) =
                        this@ViewBoardFragment.boardView.boardAdapter?.findTaskViewHolder(id)

                private inline fun findViewHolder(view: View) =
                        this@ViewBoardFragment.boardView.boardAdapter?.findTaskViewHolder(view)

                private inline fun findViewHolderUnder(point: PointF): TaskViewHolder? {
                    mainActivity.findViewUnder(point)?.also {
                        return findViewHolder(it)
                    }
                    return null
                }

                private inline fun swapTaskViewHolders(oldViewHolder: TaskViewHolder?,
                                                       newViewHolder: TaskViewHolder?) {
                    if (oldViewHolder != null && newViewHolder != null && oldViewHolder != newViewHolder) {
                        // TODO: 27-Oct-19 Something is wrong here, it works when the header
                        //  is invisible because the return point animation is actually doing
                        //  the point in relation to the DragView's parent, not the actual
                        //  RecyclerView, we need to offset this
                        //  the 2 viewParents of interest are the TaskListView and the root
                        //  of this Fragment, we need to find the position of this
                        //  TaskListView in relation to the fragment root (which is the
                        //  parent of the DragView)

                        val newReturnPoint = PointF(newViewHolder.itemView.x, newViewHolder.itemView.y)

                        dragBehavior.returnPoint.set(newReturnPoint)

                        this@ViewBoardFragment.boardView.boardAdapter?.swapTaskViewHolders(
                                oldViewHolder, newViewHolder
                        )
                    }
                }
            }
        }
    }

    private inline fun setUpListDrag() {
        list_dragShadow {

            dragBehavior.dragListener = object : ObservableDragBehavior.SimpleDragListener() {

                // The VH we are currently dragging
                private var draggingViewHolder: BoardViewHolder? = null

                // The VH we are currently over
                private var currentViewHolder: BoardViewHolder? = null

                override fun onStartDrag(dragView: View) {
                    draggingViewHolder = findViewHolder(dragListID)

                    draggingViewHolder?.itemView?.alpha = 0F
                }

                override fun onReleaseDrag(dragView: View, touchPoint: PointF) {

                }

                override fun onEndDrag(dragView: View) {

                    draggingViewHolder?.itemView?.alpha = 1F

                }

                override fun onUpdateLocation(dragView: View, touchPoint: PointF) {
                    updateViewHolders(touchPoint)
                    checkForScroll(touchPoint)
                }

                override fun onDragStateChanged(dragView: View, newState: ObservableDragBehavior.DragState) {
                    when (newState) {
                        ObservableDragBehavior.DragState.IDLE -> {
                            list_dragShadow.isVisible = false
                        }
                        ObservableDragBehavior.DragState.DRAGGING -> {
                            list_dragShadow.alpha = 0.8F
                            list_dragShadow.isVisible = true
                        }
                        ObservableDragBehavior.DragState.SETTLING -> {
                            list_dragShadow.alpha = 1F
                        }
                    }
                }

                inline fun updateViewHolders(touchPoint: PointF) {
                    if (currentViewHolder != findViewHolderUnder(touchPoint)) {
                        currentViewHolder = findViewHolderUnder(touchPoint)

                        if (draggingViewHolder != null && currentViewHolder != null &&
                                draggingViewHolder != currentViewHolder) {

                            val newReturnPoint = PointF(currentViewHolder!!.itemView.x,
                                    currentViewHolder!!.itemView.y)

                            dragBehavior.returnPoint.set(newReturnPoint)

                            this@ViewBoardFragment.boardView.boardAdapter?.swapBoardViewHolders(
                                    draggingViewHolder!!, currentViewHolder!!
                            )
                        }
                    }
                }

                inline fun findViewHolder(id: ID) =
                        this@ViewBoardFragment.boardView.findViewHolderForItemId(id) as? BoardViewHolder

                inline fun findViewHolder(view: View) =
                        this@ViewBoardFragment.boardView.findContainingViewHolder(view) as? BoardViewHolder

                inline fun findViewHolderUnder(pointF: PointF): BoardViewHolder? {
                    mainActivity.findViewUnder(pointF)?.also {
                        return findViewHolder(it)
                    }
                    return null
                }

                inline fun checkForScroll(touchPointF: PointF) {
                    // here we check if we have to scroll something either left right or up down
                }
            }
        }
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
                        onClick = {}
                        onCancel = {
                            setBackground()
                            dismiss()
                        }
                        onConfirm = {
                            board.backgroundPhoto = it
                            board.backgroundType = BackgroundType.UNSPLASH_PHOTO
                            setBackgroundImage(it)
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
            height = MATCH_PARENT
        }
        Picasso.get().load(Uri.parse(photo.urls.regular)).apply {
            fetch()
            photo.color?.also {
                placeholder(ColorDrawable(it.toColor.toAndroidColor))
            }
            fit()
            centerCrop()
        }.into(background_imageView)
    }

    private inline fun setBackgroundColor(waqtiColor: WaqtiColor) {
        background_imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = MATCH_PARENT
            height = MATCH_PARENT
        }

        background_imageView.setImageDrawable(null)
        background_imageView.background = waqtiColor.toColorDrawable
        boardView.scrollBarColor = waqtiColor.colorScheme.text
    }

    private inline fun setColorScheme(colorScheme: ColorScheme) {
        mainActivity.setColorScheme(colorScheme)
        mainActivity.drawerLayout.boardOptions_navigationView {
            setBackgroundColor(colorScheme.main.toAndroidColor)
            boardOptions_divider {
                backgroundColor = colorScheme.text.toAndroidColor
            }
        }
        addList_floatingButton.setColorScheme(colorScheme)
        boardView.setEdgeEffectColor(colorScheme.dark)
    }

    @ForLater
    private inline fun parallaxImage(photo: UnsplashPhoto) {
        doInBackground {
            background_imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                width = WRAP_CONTENT
                height = WRAP_CONTENT
            }

            Picasso.get()
                    .load(Uri.parse(photo.urls.regular))
                    .centerCrop()
                    .into(background_imageView)

            boardView?.apply {
                addOnScrollListener(onScrolled = { dx, dy ->
                    if (horizontalScrollOffset > 0 && horizontalScrollOffset < maxHorizontalScroll) {
                        val scrollAmount = (dx.D * (photo.width.D / maxHorizontalScroll.D) * 0.25).roundToInt()
                        logE(scrollAmount)
                        background_imageView.scrollBy(scrollAmount, 0)
                    }
                })
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
