package uk.whitecrescent.waqti.frontend.fragments.parents

import uk.whitecrescent.waqti.backend.Cacheable

abstract class WaqtiViewFragment<E : Cacheable>(animate: Boolean = true) : WaqtiFragment(animate) {

    override fun setUpViews() {}

    protected abstract fun setUpViews(element: E)

}