package uk.whitecrescent.waqti.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.view.ViewBoardListFragment

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
            setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }.commit()
    }

    fun getWaqtiSharedPreferences(): SharedPreferences {
        return getSharedPreferences(WAQTI_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }
}
