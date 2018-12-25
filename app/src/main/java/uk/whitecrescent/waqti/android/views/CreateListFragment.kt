package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.viewmodels.CreateListViewModel

class CreateListFragment : Fragment() {

    companion object {
        fun newInstance() = CreateListFragment()
    }

    private lateinit var viewModel: CreateListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreateListViewModel::class.java)
    }

}
