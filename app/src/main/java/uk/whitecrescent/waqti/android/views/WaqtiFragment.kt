package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.MainActivityViewModel

open class WaqtiFragment : Fragment() {

    protected lateinit var activityViewModel: MainActivityViewModel
    protected lateinit var mainActivity: MainActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        activityViewModel = mainActivity.viewModel
    }

}