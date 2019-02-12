package uk.whitecrescent.waqti.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.blank_activity.*
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.view.ViewBoardListFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.shortSnackBar

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        @Inconvenience
        // TODO: 26-Dec-18 Transitions for all Fragments are ugly!

        @GoToFragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, ViewBoardListFragment.newInstance(), BOARD_LIST_FRAGMENT)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }.commit()

        addOnBackPressedCallback {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawers()
                true
            } else false
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                drawerLayout.hideSoftKeyboard()
            }
        })

        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            drawerLayout.closeDrawers()
            navigationView.shortSnackBar("Clicked something")
            true
        }

    }

    inline val waqtiSharedPreferences: SharedPreferences
        get() = getSharedPreferences(WAQTI_SHARED_PREFERENCES, Context.MODE_PRIVATE)
}
