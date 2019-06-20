package uk.whitecrescent.waqti.frontend.fragments.parents

import uk.whitecrescent.waqti.backend.Cacheable

abstract class WaqtiCreateFragment<E : Cacheable> : WaqtiFragment(true) {

    protected abstract fun createElement(): E

    protected abstract fun setUpViews()
}