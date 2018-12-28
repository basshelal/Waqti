package uk.whitecrescent.waqti.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.base.WaqtiCreateFragment

class CreateBoardFragment : WaqtiCreateFragment() {

    companion object {
        fun newInstance() = CreateBoardFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_board, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
