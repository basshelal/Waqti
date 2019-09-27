@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.text.Editable
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
import android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import org.jetbrains.anko.colorAttr
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.toColor

/**
 * The most basic version of an Editable TextView, used across the app especially in the [AppBar]
 */
class EditTextView
@JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatEditText(context, attributeSet, defStyle) {

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
                setRawInputType(TYPE_CLASS_TEXT or
                        TYPE_TEXT_FLAG_AUTO_CORRECT or
                        TYPE_TEXT_FLAG_CAP_SENTENCES)
            }
        }

    private var currentWatcher: TextWatcher? = null

    var textChangedListener: (Editable?) -> Unit = { }
        set(value) {
            removeTextChangedListener(currentWatcher)
            currentWatcher = doAfterTextChanged(value)
        }

    init {

        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextView)

        isEditable = attrs.getBoolean(R.styleable.EditTextView_isEditable, true)

        isMultiLine = attrs.getBoolean(R.styleable.EditTextView_isMultiline, true)

        resetTextColor()

        attrs.recycle()
    }

    fun resetTextColor() {
        textColor = colorAttr(R.attr.colorOnSurface)
        hintTextColor = colorAttr(R.attr.colorOnSurface).toColor.withTransparency("7F").toAndroidColor
    }

}