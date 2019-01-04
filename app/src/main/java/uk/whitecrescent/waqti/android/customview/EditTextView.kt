package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import uk.whitecrescent.waqti.R

/**
 * The most basic version of an Editable TextView, used across the app.
 * Don't forget to manually set your input types in XML, this cannot be done here successfully
 * because of compatibility issues with API Levels < 23
 */
class EditTextView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : AppCompatEditText(context, attributeSet, defStyle) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline4)
            setTextColor(resources.getColor(R.color.black, null))
        } else {
            @Suppress("DEPRECATION")
            setTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline4)
            @Suppress("DEPRECATION")
            setTextColor(resources.getColor(R.color.black))
        }
        showSoftInputOnFocus = true
        isFocusableInTouchMode = true
        textAlignment = View.TEXT_ALIGNMENT_CENTER
    }
}