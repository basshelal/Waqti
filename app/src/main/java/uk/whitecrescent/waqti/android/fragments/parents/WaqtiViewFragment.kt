package uk.whitecrescent.waqti.android.fragments.parents

import android.os.Bundle
import uk.whitecrescent.waqti.model.Cacheable

abstract class WaqtiViewFragment<E : Cacheable> : WaqtiFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.setHasOptionsMenu(true)
    }

    abstract protected fun setUpViews(element: E)
}