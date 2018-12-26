package uk.whitecrescent.waqti.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.views.BoardFragment
import uk.whitecrescent.waqti.android.views.HomeFragment

// We want to have a Single Activity Application :)
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    // Fragments (since ViewModels shouldn't contain Contexts)
    val boardFragment: BoardFragment?
        get() = this.supportFragmentManager.findFragmentByTag(BOARD_FRAGMENT) as? BoardFragment?


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        // TODO: 26-Dec-18 Transitions for all Fragments are ugly!

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
