package uk.whitecrescent.waqti.frontend.customview.dialogs

import kotlinx.android.synthetic.main.dialog_timepicker.*
import org.threeten.bp.LocalDateTime
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME

class DateTimePickerDialog : MaterialDialog() {

    var initialTime: LocalDateTime = DEFAULT_TIME
    var onConfirm: (LocalDateTime) -> Unit = { }
    override val contentView = R.layout.dialog_timepicker

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