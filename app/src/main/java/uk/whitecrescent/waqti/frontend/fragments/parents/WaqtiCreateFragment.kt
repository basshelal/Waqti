package uk.whitecrescent.waqti.frontend.fragments.parents

import uk.whitecrescent.waqti.backend.Cacheable

abstract class WaqtiCreateFragment<E : Cacheable> : WaqtiFragment(true) {

    protected abstract val viewModel: WaqtiCreateFragmentViewModel<E>

    protected abstract fun createElement(): E

}

abstract class WaqtiCreateFragmentViewModel<E : Cacheable> : WaqtiFragmentViewModel() {

    /**
     * Create the element that this [WaqtiCreateFragment] is in charge of creating.
     *
     * Ideally this function will take in a created [E] already set up with options and parameters
     * from the fragment, this is the [fromFragment] parameter. Then on that [E] this function
     * can then further perform any changes to it.
     */
    abstract fun createElement(fromFragment: E): E

}