package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.getColorCompat
import uk.whitecrescent.waqti.frontend.setTextAppearanceCompat

/**
 * The most basic version of an Editable TextView, used across the app.
 * Don't forget to manually set your input types in XML, this cannot be done here successfully
 * because of compatibility issues with API Levels < 23
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
                // TODO: 13-Feb-19 This is so hard to make perfect ughhh
                // we want a Multi-Line but with an IME done button, not a new line button
                @ForLater
                @Inconvenience
                inputType = inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
        }

    init {
        isEditable = true
        isMultiLine = false
        setTextAppearanceCompat(R.style.TextAppearance_MaterialComponents_Headline4)
        setTextColor(resources.getColorCompat(R.color.black))
        textAlignment = View.TEXT_ALIGNMENT_CENTER
    }
}