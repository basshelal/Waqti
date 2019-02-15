package uk.whitecrescent.waqti.frontend.customview.dialogs

import android.view.View
import kotlinx.android.synthetic.main.dialog_confirm.*
import uk.whitecrescent.waqti.R

class ConfirmDialog : MaterialDialog() {

    var title = "Confirm Action"
    var message = ""
    var onConfirm: () -> Unit = { }
    override val contentView = R.layout.dialog_confirm

    override fun onResume() {
        super.onResume()

        dialog.apply {

            if (message == "") dialogMessage_textView.visibility = View.GONE
            else dialogMessage_textView.text = message

            dialogTitle_textView.text = title

            confirm_button.setOnClickListener {
                onConfirm()
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }

}