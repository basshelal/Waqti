package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.SimpleTextWatcher
import uk.whitecrescent.waqti.frontend.setTextAppearanceCompat

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
                setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT)
            }
        }
    private var currentTextChangedListeners = ArrayList<TextWatcher>()

    init {

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextView)

        attributes.getBoolean(R.styleable.EditTextView_isEditable, true).also {
            isEditable = it
        }

        attributes.getBoolean(R.styleable.EditTextView_isMultiline, false).also {
            isMultiLine = it
        }

        isMultiLine = true

        setTextAppearanceCompat(R.style.TextAppearance_MaterialComponents_Headline4)
        setTextColor(Color.BLACK)
        textAlignment = View.TEXT_ALIGNMENT_CENTER

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