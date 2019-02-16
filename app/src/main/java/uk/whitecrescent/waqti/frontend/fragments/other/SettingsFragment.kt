package uk.whitecrescent.waqti.frontend.fragments.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiOtherFragment

class SettingsFragment : WaqtiOtherFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity.resetStatusBarColor()
    }

    override fun finish() {

    }

}