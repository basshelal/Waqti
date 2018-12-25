package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.viewmodels.CreateTaskViewModel

class CreateTaskFragment : Fragment() {

    companion object {
        fun newInstance() = CreateTaskFragment()
    }

    private lateinit var viewModel: CreateTaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreateTaskViewModel::class.java)

        var boardID = 0L
        var listID = 0L

        if (arguments != null) {
            boardID = arguments!!["boardID"] as Long
            listID = arguments!!["listID"] as Long
        }

        send_button.setOnClickListener {
            val text = taskName_editText.text
            if (text != null) {
                (it.context as MainActivity).supportFragmentManager.beginTransaction().apply {
                    val fragment = BoardFragment.newInstance()
                    val bundle = Bundle()
                    bundle.putLong("boardID", boardID)
                    fragment.arguments = bundle
                    replace(R.id.blank_constraintLayout, fragment, "Board")
                    remove(this@CreateTaskFragment)
                    commit()
                }
            }
        }
    }

}
