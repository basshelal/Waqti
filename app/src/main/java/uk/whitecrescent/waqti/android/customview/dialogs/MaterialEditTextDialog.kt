package uk.whitecrescent.waqti.android.customview.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_edit_text_material.*
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.android.openKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches

class MaterialEditTextDialog : WaqtiMaterialDialog() {

    lateinit var dialog: BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(activity!!)
        dialog.setTitle(R.string.renameTask)
        dialog.setContentView(R.layout.dialog_edit_text_material)
        return dialog
    }

    override fun onResume() {
        super.onResume()

        dialog.apply {

            confirm_button.setOnClickListener {
                val text = materialDialog_editText.text
                if (text == null || text.isEmpty() || text.isBlank()) {
                    materialDialog_editText.setHintTextColor(Color.RED)
                    materialDialog_editText.hint = "Task Name cannot be empty!"
                } else {
                    Caches.tasks[viewModel.taskID].changeName(text.toString())
                    (mainActivity.supportFragmentManager.findFragmentByTag(VIEW_TASK_FRAGMENT) as ViewTaskFragment)
                            .updateText()
                    dialog.dismiss()
                }
            }
        }
        @Inconvenience
        // doesn't focus
        dialog.materialDialog_editText.openKeyboard()
    }

}