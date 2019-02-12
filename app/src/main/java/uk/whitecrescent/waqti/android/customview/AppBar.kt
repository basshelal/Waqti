package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.mainActivity

/**
 * A Material-like AppBar that allows for an ImageView on the start, an [EditTextView] in the
 * center and another ImageView on the end.
 * The ImageView on the start is meant to be the navigation drawer menu, as it will open the
 * navigation drawer by default when clicked.
 * The ImageView on the end is meant to be the options menu, as it will open the passed in menu
 * in [R.styleable.AppBar_rightClickOpensMenu] by default if one was passed in.
 * You can change the source Image of each ImageView by using the XML Parameters
 * [R.styleable.AppBar_leftImage] and [R.styleable.AppBar_rightImage] as well as the hint of the
 * EditTextView using [R.styleable.AppBar_hint]. You can also, of course, access all the contents
 * of the AppBar by getting the Views by their Ids, [leftImageView], [editTextView] and
 * [rightImageView].
 */
class AppBar
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : CardView(context, attributeSet, defStyle) {

    init {

        View.inflate(context, R.layout.view_appbar, this)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.AppBar)

        leftImageView.apply {
            attributes.getDrawable(R.styleable.AppBar_leftImage).apply {
                if (this == null) setImageResource(R.drawable.menu_icon)
                else setImageDrawable(attributes.getDrawable(R.styleable.AppBar_leftImage))
            }
            attributes.getBoolean(R.styleable.AppBar_leftClickOpensDrawer, true).apply {
                if (this == true) setOnClickListener {
                    mainActivity.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        editTextView.apply {
            attributes.getString(R.styleable.AppBar_hint).apply {
                if (this == null) hint = ""
                else hint = this
            }
        }

        rightImageView.apply {
            attributes.getDrawable(R.styleable.AppBar_leftImage).apply {
                if (this == null) setImageResource(R.drawable.overflow_icon)
                else setImageDrawable(attributes.getDrawable(R.styleable.AppBar_rightImage))
            }
            attributes.getResourceId(R.styleable.AppBar_rightClickOpensMenu, -1).apply {
                if (this != -1) setOnClickListener {
                    // TODO: 12-Feb-19 Inflate menu
                }
            }
        }

        attributes.recycle()
    }

}