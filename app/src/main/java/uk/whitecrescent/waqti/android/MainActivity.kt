package uk.whitecrescent.waqti.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.blank_activity.*
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.view.ViewBoardListFragment
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

        @FutureIdea
        // TODO: 04-Feb-19 Import and export Database like what Nova Launcher does
        // you can export the Database to be shared to something like email, drive etc and thus
        // you can also import a Database, doing so will probably delete all current data but we
        // might be able to allow a merge like what Chrome does when importing bookmarks, just
        // adds them to what's already existing, options for which one the user would like is
        // probably the way to go

        @GoToFragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, ViewBoardListFragment.newInstance(), BOARD_LIST_FRAGMENT)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }.commit()

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
