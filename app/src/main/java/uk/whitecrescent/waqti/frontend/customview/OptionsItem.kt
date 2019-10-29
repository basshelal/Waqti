package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import com.github.florent37.shapeofview.shapes.RoundRectView
import kotlinx.android.synthetic.main.options_item.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.appearance.toColor

class OptionsItem
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RoundRectView(context, attributeSet, defStyle) {
    init {

        View.inflate(context, R.layout.options_item, this)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.OptionsItem)

        attributes.getDimension(R.styleable.OptionsItem_radius, 0F).also {
            bottomLeftRadius = it
            topLeftRadius = it
            topRightRadius = it
            bottomRightRadius = it
        }

        optionsItem_imageView {
            attributes.getResourceId(R.styleable.OptionsItem_image, Int.MIN_VALUE).also {
                if (it != Int.MIN_VALUE) setImageDrawable(context.getDrawable(it))
                else {
                    setImageDrawable(null)
                    visibility = View.GONE
                }
            }
        }

        optionsItem_textView {
            attributes.getString(R.styleable.OptionsItem_text).also {
                if (it == null) text = SpannableStringBuilder("")
                else text = SpannableStringBuilder(it)
            }
        }

        optionsItem_constraintLayout {
            attributes.getColor(R.styleable.OptionsItem_color, Int.MIN_VALUE).also {
                if (it != Int.MIN_VALUE) setBackgroundColor(it)
                else setBackgroundColor(context.getColor(R.color.white))
            }
        }

        attributes.getColor(R.styleable.OptionsItem_tint, Int.MIN_VALUE).also {
            if (it != Int.MIN_VALUE) setTint(it.toColor)
            else setTint(WaqtiColor.BLACK)
        }

        attributes.recycle()
    }

    fun setTint(waqtiColor: WaqtiColor) {
        optionsItem_imageView { image?.setTint(waqtiColor.toAndroidColor) }
        optionsItem_textView { textColor = waqtiColor.toAndroidColor }
    }
}