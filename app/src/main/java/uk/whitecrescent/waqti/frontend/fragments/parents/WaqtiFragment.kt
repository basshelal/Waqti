package uk.whitecrescent.waqti.frontend.fragments.parents

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.MainActivityViewModel

abstract class WaqtiFragment(val animate: Boolean = true) : Fragment() {

    protected lateinit var mainActivityVM: MainActivityViewModel
    protected lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (animate) {
            TransitionInflater.from(context)
                    .inflateTransition(R.transition.fade_transit)
                    .also {

                        enterTransition = it

                        returnTransition = it

                        reenterTransition = it

                        exitTransition = it

                        allowEnterTransitionOverlap = true

                        allowReturnTransitionOverlap = true
                    }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        mainActivityVM = mainActivity.viewModel
    }

    protected abstract fun setUpViews()

    protected abstract fun finish()

}