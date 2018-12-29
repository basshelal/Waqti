package uk.whitecrescent.waqti.android.customview.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_edit_text.*
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.android.openKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches

class EditTextDialog : WaqtiDialog() {

    lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity != null) {
            dialog = AlertDialog.Builder(activity!!).also {
                setData(it)
                setUI(it)
                setButtons(it)
            }.create()
            return dialog
        } else throw IllegalStateException("Activity cannot be null")
    }

    override fun onResume() {
        super.onResume()
        dialog.apply {

            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val text = dialog_editText.text
                if (text == null || text.isEmpty() || text.isBlank()) {
                    dialog_editText.setHintTextColor(Color.RED)
                    dialog_editText.hint = "Task Name cannot be empty!"
                } else {
                    Caches.tasks[viewModel.taskID].changeName(text.toString())
                    (mainActivity.supportFragmentManager.findFragmentByTag(VIEW_TASK_FRAGMENT) as ViewTaskFragment)
                            .updateText()
                    dialog.dismiss()
                }
            }
            getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                dialog.cancel()
            }
        }
        @Inconvenience
        // doesn't focus
        focusTextView()
    }

    private fun setData(builder: AlertDialog.Builder) {
        builder.apply {
            setTitle(R.string.renameTask)
        }
    }

    private fun setUI(builder: AlertDialog.Builder) {
        builder.apply {
            setView(R.layout.dialog_edit_text)
        }
    }

    private fun setButtons(builder: AlertDialog.Builder) {
        builder.apply {
            setPositiveButton(R.string.confirm, null)
            setNegativeButton(R.string.cancel, null)
        }
    }

    private fun focusTextView() {
        dialog.dialog_editText.openKeyboard()
    }
}
