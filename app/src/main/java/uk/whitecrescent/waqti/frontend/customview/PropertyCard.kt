package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.property_card.view.*
import uk.whitecrescent.waqti.R

class PropertyCard
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : CardView(context, attributeSet, defStyle) {

    lateinit var popupMenu: PopupMenu
        private set
    var text: CharSequence
        set(value) {
            title_textView.text = value
        }
        get() = title_textView.text

    init {

        View.inflate(context, R.layout.property_card, this)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.PropertyCard)

        title_textView.apply {
            attributes.getString(R.styleable.PropertyCard_title).apply {
                if (this == null) text = SpannableStringBuilder("")
                else text = SpannableStringBuilder(this)
            }
        }

        overflow_imageButton.apply {
            attributes.getResourceId(R.styleable.PropertyCard_optionsMenu, Int.MIN_VALUE).apply {
                if (this != Int.MIN_VALUE) {
                    popupMenu = PopupMenu(context, overflow_imageButton)
                    popupMenu.inflate(this)
                    overflow_imageButton.setOnClickListener { popupMenu.show() }
                }
            }
        }

        attributes.recycle()
    }

    inline fun onClick(crossinline onClick: () -> Unit) {
        root_cardView.setOnClickListener {
            onClick()
        }
    }

    fun onOptionsClicked(onClick: (MenuItem) -> Boolean) {
        popupMenu.setOnMenuItemClickListener(onClick)
    }

}