package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.text.Editable
import android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
import android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.SimpleTextWatcher

/**
 * The most basic version of an Editable TextView, used across the app especially in the [AppBar]
 */
class EditTextView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : AppCompatEditText(context, attributeSet, defStyle) {

    var isEditable: Boolean = true
        set(value) {
            field = value
            isCursorVisible = value
            showSoftInputOnFocus = value
            isFocusableInTouchMode = value
            isFocusable = value
            isClickable = value
        }
    var isMultiLine: Boolean = false
        set(value) {
            field = value
            if (value) {
                imeOptions = EditorInfo.IME_ACTION_DONE
                setRawInputType(TYPE_TEXT_FLAG_CAP_SENTENCES or TYPE_TEXT_FLAG_MULTI_LINE or TYPE_TEXT_FLAG_AUTO_CORRECT)
            }
        }
    private var currentTextChangedListeners = ArrayList<TextWatcher>()

    init {

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextView)

        attributes.getBoolean(R.styleable.EditTextView_isEditable, true).also {
            isEditable = it
        }

        attributes.getBoolean(R.styleable.EditTextView_isMultiline, true).also {
            isMultiLine = it
        }

        attributes.recycle()
    }

    inline operator fun invoke(apply: EditTextView.() -> Unit) = this.apply(apply)

    fun removeAllTextChangedListeners() {
        currentTextChangedListeners.forEach {
            removeTextChangedListener(it)
        }
    }

    fun addAfterTextChangedListener(func: (Editable?) -> Unit) {
        object : SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                func(editable)
            }
        }.also {
            addTextChangedListener(it)
            currentTextChangedListeners.add(it)
        }
    }

}