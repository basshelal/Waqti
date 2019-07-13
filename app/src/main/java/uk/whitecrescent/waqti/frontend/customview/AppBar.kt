@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.drawable.Drawable
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
import uk.whitecrescent.waqti.frontend.ANY_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.hideKeyboard
import uk.whitecrescent.waqti.mainActivity

/**
 * A Material-like AppBar that allows for an ImageView on the start, an [EditTextView] in the
 * center and another ImageView on the end.
 * The ImageView on the start is meant to be the navigation drawer menu, as it will open the
 * navigation drawer by default when clicked.
 * The ImageView on the end is meant to be the options menu, as it will open the passed in menu
 * in [rightImageDefault].
 */
class AppBar
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : Toolbar(context, attributeSet, defStyle) {

    val DEFAULT_ELEVATION = 16F

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

    inline var rightImage: Drawable
        set(value) = rightImageView.setImageDrawable(value)
        get() = rightImageView.drawable

    init {

        View.inflate(context, R.layout.view_appbar, this)

        color = WaqtiColor.WHITE
        elevation = DEFAULT_ELEVATION
    }

    inline fun leftImageMenu() {
        leftImageView.apply {
            leftImageView.visibility = View.VISIBLE
            leftImage = context.getDrawable(R.drawable.menu_icon)!!
            leftImage.setTint(WaqtiColor.WAQTI_WHITE.toAndroidColor)
            setOnClickListener {
                mainActivity.drawerLayout.apply {
                    hideKeyboard()
                    openDrawer(GravityCompat.START)
                }
            }
        }
    }

    inline fun leftImageBack() {
        leftImageView.apply {
            leftImageView.visibility = View.VISIBLE
            leftImage = context.getDrawable(R.drawable.back_icon)!!
            leftImage.setTint(WaqtiColor.WAQTI_WHITE.toAndroidColor)
            setOnClickListener {
                @FragmentNavigation(from = ANY_FRAGMENT, to = ANY_FRAGMENT)
                mainActivity.supportFragmentManager.popBackStack()
            }
        }
    }

    inline fun rightImageDefault(@MenuRes menuRes: Int,
                                 noinline popupMenuOnItemClicked: (MenuItem) -> Boolean) {
        rightImageView.apply {
            rightImageView.visibility = View.VISIBLE
            rightImage = context.getDrawable(R.drawable.overflow_icon)!!
            rightImage.setTint(WaqtiColor.WAQTI_WHITE.toAndroidColor)
            setOnClickListener {
                PopupMenu(context, rightImageView).apply {
                    inflate(menuRes)
                    setOnMenuItemClickListener(popupMenuOnItemClicked)
                    show()
                }
            }
        }
    }

    override fun setBackgroundColor(color: Int) {
        background = materialShapeDrawable.apply { setTint(color) }
    }

}