package uk.whitecrescent.waqti.android.customview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_timepicker_material.*
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME

class MaterialDateTimePickerDialog : WaqtiMaterialDialog() {

    lateinit var dialog: BottomSheetDialog

    var onConfirm: (Time) -> Unit = { t -> }
    var initialTime: Time = DEFAULT_TIME

    var onCancel: View.OnClickListener = View.OnClickListener {
        this@MaterialDateTimePickerDialog.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(uk.whitecrescent.waqti.R.layout.dialog_timepicker_material)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onResume() {
        super.onResume()

        dialog.apply {

            dialogTitle_textView.text = getString(uk.whitecrescent.waqti.R.string.selectDate)

            confirm_button.text = getString(uk.whitecrescent.waqti.R.string.next)

            if (initialTime != DEFAULT_TIME)
                dateTimePicker.setInitialTime(initialTime)

            confirm_button.setOnClickListener {
                dateTimePicker.switchToTimePicker()

                dialogTitle_textView.text = getString(uk.whitecrescent.waqti.R.string.selectTime)
                confirm_button.text = getString(uk.whitecrescent.waqti.R.string.confirm)

                confirm_button.setOnClickListener {
                    onConfirm(dateTimePicker.time)
                }
            }

            cancel_button.setOnClickListener(onCancel)

        }
    }

}