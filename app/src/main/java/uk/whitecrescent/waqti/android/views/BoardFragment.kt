package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.customview.BoardAdapter
import uk.whitecrescent.waqti.android.viewmodels.BoardViewModel
import uk.whitecrescent.waqti.model.now

class BoardFragment : Fragment() {

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BoardViewModel::class.java)

        var boardID = 0L

        if (arguments != null) {
            boardID = arguments!!["boardID"] as Long
        }

        boardView.adapter = BoardAdapter(boardID)

        (activity as? MainActivity)?.supportActionBar?.title =
                "Waqti - ${boardView.boardAdapter.board.name} ${boardView.boardAdapter.boardID} " +
                "DEV BUILD"


        add_button.setOnClickListener {
            boardView.addNewEmptyList("New List @$now")
            /*activity?.supportFragmentManager?.beginTransaction().apply {
                this?.replace(R.id.blank_constraintLayout, CreateTaskFragment.newInstance(), "Create Task")
                this?.addToBackStack("At Create Task, Back goes to Board")
                this?.commit()
            }*/
        }
    }

}
