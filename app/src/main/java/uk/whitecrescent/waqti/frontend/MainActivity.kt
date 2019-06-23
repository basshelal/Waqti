package uk.whitecrescent.waqti.frontend

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.blank_activity.*
import org.jetbrains.anko.displayMetrics
import uk.whitecrescent.waqti.BuildConfig
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.addOnBackPressedCallback
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.doInBackgroundDelayed
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.AppBar
import uk.whitecrescent.waqti.frontend.fragments.other.AboutFragment
import uk.whitecrescent.waqti.frontend.fragments.other.SettingsFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewBoardListFragment
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.size

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    val currentTouchPoint = Point()

    lateinit var waqtiPreferences: WaqtiPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        viewModel = getViewModel()

        waqtiPreferences = WaqtiPreferences(this)

        if (supportFragmentManager.fragments.size == 0) {
            supportFragmentManager.commitTransaction {
                add(R.id.fragmentContainer, ViewBoardListFragment(), BOARD_LIST_FRAGMENT)
            }
        }

        drawerLayout.apply {
            addOnBackPressedCallback {
                appBar.clearFocusAndHideSoftKeyboard()
                if (isDrawerOpen(navigationView)) {
                    closeDrawers()
                    return@addOnBackPressedCallback
                }
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else this@MainActivity.finish()
            }
        }

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.allBoards_navDrawerItem -> {
                    doInBackgroundDelayed(300) {
                        popAllFragmentsInBackStack()
                    }
                }
                R.id.settings_navDrawerItem -> {
                    @GoToFragment
                    doInBackgroundDelayed(300) {
                        supportFragmentManager.commitTransaction {
                            replace(R.id.fragmentContainer, SettingsFragment(), SETTINGS_FRAGMENT)
                            addToBackStack(null)
                        }
                    }

                }
                R.id.about_navDrawerItem -> {
                    doInBackgroundDelayed(300) {
                        @GoToFragment
                        supportFragmentManager.commitTransaction {
                            replace(R.id.fragmentContainer, AboutFragment(), ABOUT_FRAGMENT)
                            addToBackStack(null)
                        }
                    }
                }
            }
            true
        }

        if (BuildConfig.DEBUG && Database.tasks.size < 100) {
            Caches.seed(5, 20, 100)
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
        supportFragmentManager.doInBackground {
            while (backStackEntryCount > 0) {
                popBackStackImmediate()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val s = super.dispatchTouchEvent(event)
        currentTouchPoint.set(event.rawX.toInt(), event.rawY.toInt())
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (appBar.editTextView.isVisible) {
                val viewRect = Rect()
                appBar.editTextView.getGlobalVisibleRect(viewRect)
                if (!viewRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    appBar.editTextView.clearFocusAndHideSoftKeyboard()
                }
            }
        }
        return s
    }

    inline val appBar: AppBar
        get() = activity_appBar

    inline val dimensions: Pair<Int, Int>
        get() = this.resources
                .let { displayMetrics.widthPixels to displayMetrics.heightPixels }
}
