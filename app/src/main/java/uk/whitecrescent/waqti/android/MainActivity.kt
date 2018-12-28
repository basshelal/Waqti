package uk.whitecrescent.waqti.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.BoardFragment
import uk.whitecrescent.waqti.android.fragments.HomeFragment
import uk.whitecrescent.waqti.model.Inconvenience

// We want to have a Single Activity Application :)
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    // Our Fragments (since ViewModels shouldn't contain Contexts)
    val homeFragment: HomeFragment?
        get() = this.supportFragmentManager.findFragmentByTag(HOME_FRAGMENT) as? HomeFragment

    val boardFragment: BoardFragment?
        get() = this.supportFragmentManager.findFragmentByTag(BOARD_FRAGMENT) as? BoardFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        @Inconvenience
        // TODO: 26-Dec-18 Transitions for all Fragments are ugly!

        @GoToFragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.blank_constraintLayout, HomeFragment.newInstance(), HOME_FRAGMENT)
            setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: 26-Dec-18 What to do with this?? Below
        //logE("Caches closed!")
        //Caches.close()
    }
}
