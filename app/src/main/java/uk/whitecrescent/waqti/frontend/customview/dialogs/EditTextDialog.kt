package uk.whitecrescent.waqti.frontend.customview.dialogs

import android.text.SpannableStringBuilder
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.dialog_edit_text.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.extensions.clearFocusAndHideKeyboard

class EditTextDialog : MaterialDialog() {

    var hint: String = ""
    var initialText: String = ""
    var onConfirm: (String) -> Unit = { }
    override val contentView = R.layout.dialog_edit_text

    override fun onResume() {
        super.onResume()

        dialog.apply {

            dialog_editTextView.apply {
                hint = this@EditTextDialog.hint
                if (initialText != "") text = SpannableStringBuilder(initialText)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }


            confirm_button.setOnClickListener {
                onConfirm(dialog_editTextView.text.toString())
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }
}