package uk.whitecrescent.waqti.android.fragments.parents

import uk.whitecrescent.waqti.model.Cacheable

abstract class WaqtiCreateFragment<E : Cacheable> : WaqtiFragment() {

    abstract protected fun createElement(): E

    abstract protected fun setUpViews()
}