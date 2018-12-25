package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.viewmodels.ViewTaskViewModel

class ViewTaskFragment : Fragment() {

    companion object {
        fun newInstance() = ViewTaskFragment()
    }

    private lateinit var viewModel: ViewTaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ViewTaskViewModel::class.java)
    }

}
