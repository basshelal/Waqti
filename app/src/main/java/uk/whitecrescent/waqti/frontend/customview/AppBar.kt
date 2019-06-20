@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import org.jetbrains.anko.internals.AnkoInternals
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.removeOnClickListener

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
                          defStyle: Int = 0) : Toolbar(context, attributeSet, defStyle) {

    val DEFAULT_ELEVATION = 16F

    lateinit var popupMenu: PopupMenu
    private val materialShapeDrawable = MaterialShapeDrawable(context, attributeSet, defStyle, 0)

    inline val leftImageView: ImageView get() = appBar_leftImageView
    inline val rightImageView: ImageView get() = appBar_rightImageView
    inline val editTextView: EditTextView get() = appBar_editTextView

    inline var color: WaqtiColor
        @Deprecated(AnkoInternals.NO_GETTER, level = DeprecationLevel.ERROR) get() = AnkoInternals.noGetter()
        set(value) = setBackgroundColor(value.toAndroidColor)

    inline var leftImage: Drawable
        set(value) = leftImageView.setImageDrawable(value)
        get() = leftImageView.drawable

    inline var leftImageIsVisible: Boolean
        set(value) {
            if (value) leftImageView.visibility = View.VISIBLE
            else leftImageView.visibility = View.INVISIBLE
        }
        get() = leftImageView.visibility == View.VISIBLE

    inline var rightImage: Drawable
        set(value) = rightImageView.setImageDrawable(value)
        get() = rightImageView.drawable

    inline var rightImageIsVisible: Boolean
        set(value) {
            if (value) rightImageView.visibility = View.VISIBLE
            else rightImageView.visibility = View.INVISIBLE
        }
        get() = rightImageView.visibility == View.VISIBLE

    init {

        View.inflate(context, R.layout.view_appbar, this)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.AppBar)

        color = WaqtiColor.WHITE
        elevation = DEFAULT_ELEVATION

        leftImageView.apply {
            attributes.getDrawable(R.styleable.AppBar_leftImage).also {
                if (it == null) leftImage = context.getDrawable(R.drawable.menu_icon)!!
                else leftImage = it
            }
            // remove, should be done by getting the leftImageView
            attributes.getBoolean(R.styleable.AppBar_leftClickOpensDrawer, true).also {
                if (it == true) setOnClickListener {
                    mainActivity.drawerLayout.apply {
                        hideSoftKeyboard()
                        openDrawer(GravityCompat.START)
                    }
                }
            }
        }

        // remove all of this, should be done by caller
        editTextView.apply {
            attributes.getString(R.styleable.AppBar_text).also {
                if (it == null) text = SpannableStringBuilder("")
                else text = SpannableStringBuilder(it)
            }
            attributes.getString(R.styleable.AppBar_hint).also {
                if (it == null) hint = ""
                else hint = it
            }
            // remove
            if (!attributes.getBoolean(R.styleable.AppBar_editable, true)) {
                isEditable = false
            }
            // remove
            if (attributes.getBoolean(R.styleable.AppBar_isMultiLine, false)) {
                isMultiLine = true
            }
        }

        rightImageView.apply {
            if (attributes.getBoolean(R.styleable.AppBar_hasRightImage, true)) {
                rightImageIsVisible = true
                attributes.getDrawable(R.styleable.AppBar_rightImage).also {
                    if (it == null) rightImage = context.getDrawable(R.drawable.overflow_icon)!!
                    else rightImage = it
                }
                // remove, should be done by getting the rightImageView
                attributes.getResourceId(R.styleable.AppBar_rightClickOpensMenu, Int.MIN_VALUE).also {
                    if (it != Int.MIN_VALUE) {
                        popupMenu = PopupMenu(context, rightImageView)
                        popupMenu.inflate(it)
                        setOnClickListener { popupMenu.show() }
                    }
                }
            } else rightImageIsVisible = false
        }

        attributes.recycle()
    }

    inline operator fun invoke(apply: AppBar.() -> Unit): AppBar = this.apply(apply)

    inline fun leftImageDefault() {
        leftImageView.apply {
            leftImageIsVisible = true
            leftImage = context.getDrawable(R.drawable.menu_icon)!!
            setOnClickListener {
                mainActivity.drawerLayout.apply {
                    hideSoftKeyboard()
                    openDrawer(GravityCompat.START)
                }
            }
        }
    }

    inline fun rightImageDefault(@MenuRes menuRes: Int,
                                 noinline popupMenuOnItemClicked: (MenuItem) -> Boolean) {
        rightImageView.apply {
            removeOnClickListener()
            rightImageIsVisible = true
            rightImage = context.getDrawable(R.drawable.overflow_icon)!!

            popupMenu = PopupMenu(context, rightImageView)
            popupMenu.inflate(menuRes)
            popupMenu.setOnMenuItemClickListener(popupMenuOnItemClicked)
            setOnClickListener { popupMenu.show() }
        }
    }

    // remove, should be done in caller
    fun popupMenuOnItemClicked(onClick: (MenuItem) -> Boolean) {
        popupMenu.setOnMenuItemClickListener(onClick)
    }

    inline fun setBackgroundColor(color: WaqtiColor) {
        setBackgroundColor(color.toAndroidColor)
    }

    override fun setBackgroundColor(color: Int) {
        background = materialShapeDrawable.apply { setTint(color) }
    }

}