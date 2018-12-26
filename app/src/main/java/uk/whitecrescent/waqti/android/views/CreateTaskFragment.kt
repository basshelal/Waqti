package uk.whitecrescent.waqti.android.views

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.viewmodels.CreateTaskViewModel
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.Task

class CreateTaskFragment : WaqtiFragment() {

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
            if (text == null || text.isEmpty() || text.isBlank()) {
                taskName_editText.setHintTextColor(Color.RED)
                taskName_editText.hint = "Task Name cannot be empty!"
            } else {
                val task = createTaskFromEditText()
                Caches.boards[boardID][listID].add(task).update()
                taskName_editText.hideSoftKeyboard()
                mainActivity.supportFragmentManager.popBackStack()
            }
        }
    }

    fun createTaskFromEditText(): Task {
        return Task(taskName_editText.text.toString())
    }

}
