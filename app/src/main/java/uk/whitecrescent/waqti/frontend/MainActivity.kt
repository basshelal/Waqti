package uk.whitecrescent.waqti.frontend

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.blank_activity.*
import org.jetbrains.anko.displayMetrics
import uk.whitecrescent.waqti.BuildConfig
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.AppBar
import uk.whitecrescent.waqti.frontend.customview.EditTextView
import uk.whitecrescent.waqti.frontend.fragments.other.AboutFragment
import uk.whitecrescent.waqti.frontend.fragments.other.SettingsFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewBoardListFragment
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.size

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    /**
     * The [EditTextView] that will lose focus when the user clicks anywhere outside it,
     * this is the [EditTextView] of the [AppBar]
     */
    lateinit var hideableEditTextView: EditTextView

    @Inconvenience
    // TODO: 26-Dec-18 Transitions for all Fragments are ugly!
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        viewModel = getViewModel()

        if (supportFragmentManager.fragments.size == 0) {
            @GoToFragment
            supportFragmentManager.commitTransaction {
                add(R.id.fragmentContainer, ViewBoardListFragment(), BOARD_LIST_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }

        drawerLayout.apply {
            addOnBackPressedCallback {
                if (isDrawerOpen(navigationView)) {
                    closeDrawers()
                    true
                } else false
            }
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerOpened(drawerView: View) = clearFocusAndHideSoftKeyboard()
            })
        }

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.allBoards_navDrawerItem -> {
                    popAllFragmentsInBackStack()
                }
                R.id.about_navDrawerItem -> {
                    @GoToFragment
                    supportFragmentManager.commitTransaction {
                        replace(R.id.fragmentContainer, AboutFragment(), ABOUT_FRAGMENT)
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        addToBackStack("")
                    }
                }
                R.id.settings_navDrawerItem -> {
                    @GoToFragment
                    supportFragmentManager.commitTransaction {
                        replace(R.id.fragmentContainer, SettingsFragment(), SETTINGS_FRAGMENT)
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        addToBackStack("")
                    }
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        navigationView.menu.removeItem(R.id.settings_navDrawerItem)

        if (BuildConfig.DEBUG && Database.tasks.size < 100) {
            Caches.seed(5, 10, 20)
        }
    }

    fun setStatusBarColor(color: WaqtiColor) {
        window.statusBarColor = color.toAndroidColor
    }

    fun setNavigationBarColor(color: WaqtiColor) {
        window.navigationBarColor = color.toAndroidColor
    }

    fun resetNavBarStatusBarColor() {
        setStatusBarColor(WaqtiColor("#560027"))
        setNavigationBarColor(WaqtiColor.WAQTI_DEFAULT)
    }

    fun popAllFragmentsInBackStack() {
        supportFragmentManager.apply {
            while (backStackEntryCount > 0) {
                popBackStackImmediate()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (hideableEditTextView.isVisible) {
                val viewRect = Rect()
                hideableEditTextView.getGlobalVisibleRect(viewRect)
                if (!viewRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideableEditTextView.clearFocusAndHideSoftKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    inline val waqtiSharedPreferences: SharedPreferences
        get() = getSharedPreferences(WAQTI_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    inline val dimensions: Pair<Int, Int>
        get() = this.resources
                .let { displayMetrics.widthPixels to displayMetrics.heightPixels }
}
