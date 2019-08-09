@file:Suppress("NOTHING_TO_INLINE")

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
import org.jetbrains.anko.colorAttr
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.SimpleTextWatcher
import uk.whitecrescent.waqti.frontend.appearance.toColor

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
    private var currentTextChangedListeners = ArrayList<TextWatcher>()

    init {

        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextView)

        isEditable = attrs.getBoolean(R.styleable.EditTextView_isEditable, true)

        isMultiLine = attrs.getBoolean(R.styleable.EditTextView_isMultiline, true)

        hintTextColor = colorAttr(R.attr.colorOnSurface).toColor.withTransparency("7F").toAndroidColor

        attrs.recycle()
    }

    fun removeAllTextChangedListeners() {
        currentTextChangedListeners.forEach {
            removeTextChangedListener(it)
        }
    }

    fun resetTextColor() {
        textColor = context.colorAttr(R.attr.colorOnSurface)
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

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun updateState(apply: State.() -> Unit): EditTextView {
        state.apply(apply)
        return updateUI()
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun updateUI(): EditTextView {
        // TODO update UI
        return this
    }

    inner class State : StatefulView.ViewState()

}