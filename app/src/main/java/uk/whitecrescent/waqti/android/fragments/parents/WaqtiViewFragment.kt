package uk.whitecrescent.waqti.android.fragments.parents

import uk.whitecrescent.waqti.model.Cacheable

abstract class WaqtiViewFragment<E : Cacheable> : WaqtiFragment() {

    abstract protected fun setUpViews(element: E)
}