@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.UpdateDocumentation
import uk.whitecrescent.waqti.frontend.ANY_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.hideKeyboard
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity

@UpdateDocumentation
/**
 * A Material-like AppBar that allows for an ImageView on the start, an [EditTextView] in the
 * center and another ImageView on the end.
 * The ImageView on the start is meant to be the navigation drawer menu, as it will open the
 * navigation drawer by default when clicked.
 * The ImageView on the end is meant to be the options menu, as it will open the passed in menu
 * in [rightImageOptions].
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

    inline var leftImage: Drawable
        set(value) = leftImageView.setImageDrawable(value)
        get() = leftImageView.drawable

    inline var rightImage: Drawable
        set(value) = rightImageView.setImageDrawable(value)
        get() = rightImageView.drawable

    init {

        View.inflate(context, R.layout.view_appbar, this)

        setColorScheme(ColorScheme.WAQTI_DEFAULT)
        elevation = DEFAULT_ELEVATION
    }

    inline fun leftImageMenu() {
        leftImageView {
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
        leftImageView {
            leftImageView.visibility = View.VISIBLE
            leftImage = context.getDrawable(R.drawable.back_icon)!!
            setOnClickListener {
                @FragmentNavigation(from = ANY_FRAGMENT, to = ANY_FRAGMENT)
                mainActivity.supportFragmentManager.popBackStack()
            }
        }
    }

    inline fun rightImageOptions() {
        rightImageView {
            rightImageView.visibility = View.VISIBLE
            rightImage = context.getDrawable(R.drawable.overflow_icon)!!
        }
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        backgroundColor = colorScheme.main.toAndroidColor
        leftImage.setTint(colorScheme.text.toAndroidColor)
        editTextView.textColor = colorScheme.text.toAndroidColor
        rightImage.setTint(colorScheme.text.toAndroidColor)
    }

    override fun setBackgroundColor(color: Int) {
        background = materialShapeDrawable.apply { setTint(color) }
    }

}