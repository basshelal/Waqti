package uk.whitecrescent.waqti.android.fragments.parents

import android.os.Bundle
import androidx.fragment.app.Fragment
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.MainActivityViewModel

abstract class WaqtiFragment : Fragment() {

    protected lateinit var mainActivityViewModel: MainActivityViewModel
    protected lateinit var mainActivity: MainActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        mainActivityViewModel = mainActivity.viewModel
    }

    protected abstract fun finish()

}