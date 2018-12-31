package uk.whitecrescent.waqti.android.customview

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

open class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(editable: Editable?) {}

    override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {}
}

inline fun TextView.addAfterTextChangedListener(crossinline func: (Editable?) -> Unit) {
    this.addTextChangedListener(object : SimpleTextWatcher() {
        override fun afterTextChanged(editable: Editable?) {
            func(editable)
        }
    })
}