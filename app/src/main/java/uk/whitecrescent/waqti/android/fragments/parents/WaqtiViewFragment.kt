package uk.whitecrescent.waqti.android.fragments.parents

import android.os.Bundle

abstract class WaqtiViewFragment : WaqtiFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.setHasOptionsMenu(true)
    }
}