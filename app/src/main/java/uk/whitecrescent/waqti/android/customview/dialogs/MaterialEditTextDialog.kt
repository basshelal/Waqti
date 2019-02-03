package uk.whitecrescent.waqti.android.customview.dialogs

import android.text.SpannableStringBuilder
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.dialog_edit_text_material.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.hideSoftKeyboard

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

            dialog_editTextView.setOnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus()
                    textView.hideSoftKeyboard()
                    true
                } else false
            }


            confirm_button.setOnClickListener {
                onConfirm(dialog_editTextView.text.toString())
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }
}