package uk.whitecrescent.waqti.android.customview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_timepicker_material.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.Time

class MaterialDateTimePickerDialog : WaqtiMaterialDialog() {


    lateinit var dialog: BottomSheetDialog

    var onConfirm: (Time) -> Unit = { t -> }

    var onCancel: View.OnClickListener = View.OnClickListener {
        this@MaterialDateTimePickerDialog.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(R.layout.dialog_timepicker_material)
        return dialog
    }

    override fun onResume() {
        super.onResume()

        dialog.apply {

            dialogTitle_textView.text = getString(R.string.selectDate)

            confirm_button.text = getString(R.string.next)

            confirm_button.setOnClickListener {
                dateTimePicker.switchToTimePicker()

                dialogTitle_textView.text = getString(R.string.selectTime)
                confirm_button.text = getString(R.string.confirm)

                confirm_button.setOnClickListener {
                    onConfirm(dateTimePicker.time)
                }
            }

            cancel_button.setOnClickListener(onCancel)

        }
    }

}