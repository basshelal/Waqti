package uk.whitecrescent.waqti.frontend.fragments.parents

import android.os.Bundle
import androidx.fragment.app.Fragment
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.MainActivityViewModel

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