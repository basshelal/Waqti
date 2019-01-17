package uk.whitecrescent.waqti.android.customview.dialogs

import kotlinx.android.synthetic.main.dialog_timepicker_material.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME

class MaterialDateTimePickerDialog : WaqtiMaterialDialog() {

    var initialTime: Time = DEFAULT_TIME
    var onConfirm: (Time) -> Unit = { }
    override val contentView = R.layout.dialog_timepicker_material

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