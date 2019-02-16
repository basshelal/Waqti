package uk.whitecrescent.waqti.frontend

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.blank_activity.*
import uk.whitecrescent.waqti.Bug
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.other.AboutFragment
import uk.whitecrescent.waqti.frontend.fragments.other.SettingsFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewBoardListFragment
import uk.whitecrescent.waqti.hideSoftKeyboard


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    @Bug
    // TODO: 12-Feb-19 Rotating phone is a major bug!
    @Inconvenience
    // TODO: 26-Dec-18 Transitions for all Fragments are ugly!
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(uk.whitecrescent.waqti.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(uk.whitecrescent.waqti.R.layout.blank_activity)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)


        @GoToFragment
        supportFragmentManager.beginTransaction().apply {
            add(uk.whitecrescent.waqti.R.id.fragmentContainer, ViewBoardListFragment(), BOARD_LIST_FRAGMENT)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }.commit()

        drawerLayout.apply {
            addOnBackPressedCallback {
                if (isDrawerOpen(navigationView)) {
                    closeDrawers()
                    true
                } else false
            }
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerOpened(drawerView: View) {
                    hideSoftKeyboard()
                }
            })
        }

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                uk.whitecrescent.waqti.R.id.allBoards_navDrawerItem -> {
                    popAllFragmentsInBackStack()
                }
                uk.whitecrescent.waqti.R.id.about_navDrawerItem -> {
                    @GoToFragment
                    supportFragmentManager.beginTransaction().apply {
                        replace(uk.whitecrescent.waqti.R.id.fragmentContainer, AboutFragment(), ABOUT_FRAGMENT)
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        addToBackStack("")
                    }.commit()
                }
                uk.whitecrescent.waqti.R.id.settings_navDrawerItem -> {
                    @GoToFragment
                    supportFragmentManager.beginTransaction().apply {
                        replace(uk.whitecrescent.waqti.R.id.fragmentContainer, SettingsFragment(), SETTINGS_FRAGMENT)
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        addToBackStack("")
                    }.commit()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    fun setStatusBarColor(color: WaqtiColor) {
        window.statusBarColor = color.toAndroidColor

    }

    fun setNavigationBarColor(color: WaqtiColor) {
        window.navigationBarColor = color.toAndroidColor
    }

    fun resetStatusBarColor() {
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

    inline val waqtiSharedPreferences: SharedPreferences
        get() = getSharedPreferences(WAQTI_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    /*override fun onStop() {
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.apply {
                while (backStackEntryCount > 0) {
                    popBackStackImmediate()
                }
                fragments.onEach { remove(it) }
            }
        }.commitNowAllowingStateLoss()
        super.onStop()
    }*/
}
