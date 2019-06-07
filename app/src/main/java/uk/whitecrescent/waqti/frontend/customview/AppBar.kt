package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.hideSoftKeyboard
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
                          defStyle: Int = 0) : ConstraintLayout(context, attributeSet, defStyle) {

    lateinit var popupMenu: PopupMenu
        private set

    init {

        View.inflate(context, R.layout.view_appbar, this)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.AppBar)

        leftImageView.apply {
            attributes.getDrawable(R.styleable.AppBar_leftImage).apply {
                if (this == null) setImageResource(R.drawable.menu_icon)
                else setImageDrawable(this)
            }
            attributes.getBoolean(R.styleable.AppBar_leftClickOpensDrawer, true).apply {
                if (this == true) setOnClickListener {
                    mainActivity.drawerLayout.apply {
                        hideSoftKeyboard()
                        openDrawer(GravityCompat.START)
                    }
                }
            }
        }

        editTextView.apply {
            attributes.getString(R.styleable.AppBar_text).apply {
                if (this == null) text = SpannableStringBuilder("")
                else text = SpannableStringBuilder(this)
            }
            attributes.getString(R.styleable.AppBar_hint).apply {
                if (this == null) hint = ""
                else hint = this
            }
            if (!attributes.getBoolean(R.styleable.AppBar_editable, true)) {
                isEditable = false
            }
            if (attributes.getBoolean(R.styleable.AppBar_isMultiLine, false)) {
                isMultiLine = true
            }
        }

        rightImageView.apply {
            if (attributes.getBoolean(R.styleable.AppBar_hasRightImage, true)) {
                attributes.getDrawable(R.styleable.AppBar_rightImage).apply {
                    if (this == null) setImageResource(R.drawable.overflow_icon)
                    else setImageDrawable(this)
                }
                attributes.getResourceId(R.styleable.AppBar_rightClickOpensMenu, Int.MIN_VALUE).apply {
                    if (this != Int.MIN_VALUE) {
                        popupMenu = PopupMenu(context, rightImageView)
                        popupMenu.inflate(this)
                        setOnClickListener { popupMenu.show() }
                    }
                }
            } else {
                setImageDrawable(null)
                background = null
            }

        }

        attributes.recycle()
    }

    fun popupMenuOnItemClicked(onClick: (MenuItem) -> Boolean) {
        popupMenu.setOnMenuItemClickListener(onClick)
    }

    fun setBackgroundColor(color: WaqtiColor) {
        (editTextView.parent as ConstraintLayout).setBackgroundColor(color.toAndroidColor)
    }

}