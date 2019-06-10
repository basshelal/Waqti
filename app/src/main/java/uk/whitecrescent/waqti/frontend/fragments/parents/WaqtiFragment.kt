package uk.whitecrescent.waqti.frontend.fragments.parents

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.MainActivityViewModel

abstract class WaqtiFragment : Fragment() {

    protected lateinit var mainActivityViewModel: MainActivityViewModel
    protected lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.fade_transit)
        enterTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.fade_transit)

        sharedElementReturnTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.fade_transit)
        exitTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.fade_transit)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        mainActivityViewModel = mainActivity.viewModel
    }

    protected abstract fun finish()

}