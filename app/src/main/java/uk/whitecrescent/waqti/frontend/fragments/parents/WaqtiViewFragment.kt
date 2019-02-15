package uk.whitecrescent.waqti.frontend.fragments.parents

import uk.whitecrescent.waqti.backend.Cacheable

abstract class WaqtiViewFragment<E : Cacheable> : WaqtiFragment() {

    protected abstract fun setUpViews(element: E)
}