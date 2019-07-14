package uk.whitecrescent.waqti.frontend.customview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import kotlinx.android.synthetic.main.dialog_confirm.*
import uk.whitecrescent.waqti.R

class ConfirmDialog : MaterialDialog() {

    var title = "Confirm Action"
    var message = ""
    var onConfirm: () -> Unit = { }
    override val contentView = R.layout.dialog_confirm

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        dialog.apply {

            dialogTitle_textView.text = SpannableStringBuilder(title)

            if (message == "") dialogMessage_textView.visibility = View.GONE
            else dialogMessage_textView.text = SpannableStringBuilder(message)

            confirm_button.setOnClickListener {
                onConfirm()
            }

            cancel_button.setOnClickListener(onCancel)
        }

        return dialog
    }

}