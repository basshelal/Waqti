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
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.appearance.toColor
import uk.whitecrescent.waqti.toEditable

/**
 * The most basic version of an Editable TextView, used across the app especially in the [AppBar]
 */
class EditTextView
@JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatEditText(context, attributeSet, defStyle), StatefulView<EditTextView.State> {

    override val state = State()

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

        hintTextColor = colorAttr(R.attr.colorOnSurface).toColor.withTransparency("7F").toAndroidColor

        attrs.recycle()
    }

    fun resetTextColor() {
        textColor = context.colorAttr(R.attr.colorOnSurface)
    }

    override inline fun updateState(apply: State.() -> Unit): EditTextView {
        state.apply(apply)
        return updateUI()
    }

    override inline fun updateUI(): EditTextView {
        with(state) {
            this@EditTextView.textChangedListener = textChangedListener
            this@EditTextView.isEditable = isEditable
            this@EditTextView.isMultiLine = isMultiLine
            this@EditTextView.hint = hint
            this@EditTextView.text = text.toEditable()
            this@EditTextView.hintTextColor = hintTextColor.toAndroidColor
            this@EditTextView.textColor = textColor.toAndroidColor
        }
        return this
    }

    inner class State : StatefulView.ViewState() {
        var isEditable: Boolean = true
        var isMultiLine: Boolean = false
        var hint: String = ""
        var text: String = ""
        var hintTextColor: WaqtiColor = WaqtiColor.WHITE
        var textColor: WaqtiColor = WaqtiColor.WHITE
        var textChangedListener: (Editable?) -> Unit = { }
    }

}