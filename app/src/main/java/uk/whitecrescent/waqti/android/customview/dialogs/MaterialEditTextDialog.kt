package uk.whitecrescent.waqti.android.customview.dialogs

import android.text.SpannableStringBuilder
import kotlinx.android.synthetic.main.dialog_edit_text_material.*
import uk.whitecrescent.waqti.R

class MaterialEditTextDialog : WaqtiMaterialDialog() {

    var title = "Enter Text"
    var hint = ""
    var initialText: String = ""
    var onConfirm: (String) -> Unit = { }
    override val contentView = R.layout.dialog_edit_text_material

    override fun onResume() {
        super.onResume()

        dialog.apply {

            dialogTitle_textView.text = title

            dialog_editTextView.hint = hint

            if (initialText != "") dialog_editTextView.text = SpannableStringBuilder(initialText)

            confirm_button.setOnClickListener {
                onConfirm(dialog_editTextView.text.toString())
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }
}