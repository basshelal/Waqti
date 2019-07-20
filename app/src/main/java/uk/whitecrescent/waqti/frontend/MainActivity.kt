@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.colorAttr
import org.jetbrains.anko.configuration
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.addOnBackPressedCallback
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.doInBackgroundOnceWhen
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.customview.AppBar
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.frontend.fragments.other.AboutFragment
import uk.whitecrescent.waqti.frontend.fragments.other.SettingsFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewBoardListFragment
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.onClickOutside

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel
    lateinit var preferences: WaqtiPreferences
    val currentTouchPoint = Point()
    val onTouchOutSideListeners = HashMap<View, (View) -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = WaqtiPreferences(this)
        setTheme(preferences.appTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        viewModel = getViewModel()


        setUpViews()

    }

    private inline fun setUpViews() {

        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = NO_FRAGMENT, to = VIEW_BOARD_LIST_FRAGMENT)
                add(R.id.fragmentContainer, ViewBoardListFragment(), VIEW_BOARD_LIST_FRAGMENT)
                navigationView.setCheckedItem(R.id.allBoards_navDrawerItem)
            }
        }

        drawerLayout {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
            setViewElevation(GravityCompat.END, 1F)
            addOnBackPressedCallback {
                appBar.clearFocusAndHideKeyboard()
                if (isDrawerOpen(GravityCompat.START) || isDrawerOpen(GravityCompat.END)) {
                    closeDrawers()
                    return@addOnBackPressedCallback
                }
                if (supportFragmentManager.backStackEntryCount > 0) {
                    @FragmentNavigation(from = ANY_FRAGMENT, to = PREVIOUS_FRAGMENT)
                    supportFragmentManager.popBackStack()
                } else this@MainActivity.finish()
            }
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {

                private val DEFAULT_SCRIM_COLOR = -0x67000000

                private var currentColor: Int = DEFAULT_SCRIM_COLOR
                    set(value) {
                        field = value
                        setScrimColor(value)
                    }

                override fun onDrawerOpened(drawerView: View) {
                    if (isDrawerOpen(GravityCompat.END)) {
                        setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END)
                    }
                }

                override fun onDrawerClosed(drawerView: View) {
                    if (!isDrawerOpen(GravityCompat.END)) {
                        currentColor = DEFAULT_SCRIM_COLOR
                        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
                    }
                }

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    if (isDrawerVisible(GravityCompat.END) && currentColor != Color.TRANSPARENT) {
                        currentColor = Color.TRANSPARENT
                    }
                }
            })
        }

        navigationView.backgroundColor = colorAttr(R.attr.colorSurface)

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.allBoards_navDrawerItem -> {
                    doInBackgroundOnceWhen({ !drawerLayout.isDrawerOpen(GravityCompat.START) }, {
                        @FragmentNavigation(from = ANY_FRAGMENT, to = VIEW_BOARD_LIST_FRAGMENT)
                        popAllFragmentsInBackStack()
                    })
                }
                R.id.settings_navDrawerItem -> {
                    doInBackgroundOnceWhen({ !drawerLayout.isDrawerOpen(GravityCompat.START) }, {
                        if (currentFragment.tag != SETTINGS_FRAGMENT)
                            supportFragmentManager.commitTransaction {
                                @FragmentNavigation(from = ANY_FRAGMENT, to = SETTINGS_FRAGMENT)
                                replace(R.id.fragmentContainer, SettingsFragment(), SETTINGS_FRAGMENT)
                                addToBackStack(null)
                            }
                    })
                }
                R.id.about_navDrawerItem -> {
                    doInBackgroundOnceWhen({ !drawerLayout.isDrawerOpen(GravityCompat.START) }, {
                        if (currentFragment.tag != ABOUT_FRAGMENT)
                            supportFragmentManager.commitTransaction {
                                @FragmentNavigation(from = ANY_FRAGMENT, to = ABOUT_FRAGMENT)
                                replace(R.id.fragmentContainer, AboutFragment(), ABOUT_FRAGMENT)
                                addToBackStack(null)
                            }
                    })
                }
            }
            true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            when (currentFragment.tag) {
                SETTINGS_FRAGMENT -> navigationView.setCheckedItem(R.id.settings_navDrawerItem)
                ABOUT_FRAGMENT -> navigationView.setCheckedItem(R.id.about_navDrawerItem)
                else -> navigationView.setCheckedItem(R.id.allBoards_navDrawerItem)
            }
        }

        appBar.editTextView.onClickOutside {
            it.clearFocusAndHideKeyboard()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val spr = super.dispatchTouchEvent(event)
        currentTouchPoint.set(event.rawX.toInt(), event.rawY.toInt())
        if (event.action == MotionEvent.ACTION_DOWN) {
            onTouchOutSideListeners.forEach {
                val (view, onClick) = it
                if (view.isVisible) {
                    val viewRect = Rect()
                    view.getGlobalVisibleRect(viewRect)
                    if (!viewRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        onClick(view)
                    }
                }
            }
        }
        return spr
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        appBar.setColorScheme(colorScheme)
        window.statusBarColor = colorScheme.dark.toAndroidColor
        window.navigationBarColor = colorScheme.main.toAndroidColor

        navigationView.getHeaderView(0).apply {
            setBackgroundColor(colorScheme.main.toAndroidColor)
            navigation_header_title?.textColor = colorScheme.text.toAndroidColor
        }
    }

    fun setTheme(appTheme: AppTheme) {
        when (appTheme) {
            AppTheme.LIGHT -> setTheme(R.style.AppTheme_Light)
            AppTheme.DARK -> setTheme(R.style.AppTheme_Dark)
            AppTheme.BLACK -> setTheme(R.style.AppTheme_Black)
        }
        val needsRecreate = appTheme != preferences.appTheme
        preferences.appTheme = appTheme
        if (needsRecreate) recreate()
    }

    inline fun resetColorScheme() {
        setColorScheme(ColorScheme.WAQTI_DEFAULT)
    }

    inline fun popAllFragmentsInBackStack() {
        supportFragmentManager.doInBackground {
            while (backStackEntryCount > 0) {
                popBackStackImmediate() // TODO: 09-Jul-19 Look into changing this to something more efficient
            }
        }
    }

    inline val appBar: AppBar
        get() = activity_appBar

    inline val dimensions: Pair<Int, Int>
        get() = this.resources
                .let { displayMetrics.widthPixels to displayMetrics.heightPixels }

    inline val currentFragment: Fragment
        get() = supportFragmentManager.fragments.last()

    inline val isNightMode: Boolean
        get() = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    inline val isDarkTheme: Boolean
        get() = preferences.appTheme == AppTheme.DARK || preferences.appTheme == AppTheme.BLACK
}

class MainActivityViewModel : ViewModel() {

    var boardID: ID = 0L
    var listID: ID = 0L
    var taskID: ID = 0L
    var boardListPosition = ChangedPositionPair()

    // TODO: 27-Jun-19 We need to get rid of this guy sometime
    var boardPosition = ChangedPositionPair()
    var boardAdapter: BoardAdapter? = null
    var settingsChanged = false

}
