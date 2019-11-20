@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.extensions.convertDpToPx
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.mainActivity
import uk.whitecrescent.waqti.frontend.ANY_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme

/**
 * A Material-like AppBar that allows for an ImageView on the start, an [EditTextView] in the
 * center and another ImageView on the end.
 * The ImageView on the start is meant to be the navigation drawer menu and back button.
 * The ImageView on the end is meant to be the options menu.
 */
class AppBar
@JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    companion object {
        const val DEFAULT_ELEVATION = 16F
    }

    private val materialShapeDrawable = MaterialShapeDrawable(context, attributeSet, defStyle, 0)

    inline val leftImageView: ImageView get() = appBar_leftImageView
    inline val rightImageView: ImageView get() = appBar_rightImageView
    inline val editTextView: EditTextView get() = appBar_editTextView

    inline var leftImageDrawable: Drawable?
        set(value) {
            leftImageView.setImageDrawable(value)
            resolveLeftImage()
        }
        get() = leftImageView.drawable

    var leftImage: Int = Int.MIN_VALUE
        set(value) {
            field = value
            leftImageDrawable = if (value != Int.MIN_VALUE) context.getDrawable(value) else null
        }

    inline var rightImageDrawable: Drawable?
        set(value) = rightImageView.setImageDrawable(value)
        get() = rightImageView.drawable

    var rightImage: Int = Int.MIN_VALUE
        set(value) {
            field = value
            rightImageDrawable = if (value != Int.MIN_VALUE) context.getDrawable(value) else null
        }

    init {
        View.inflate(context, R.layout.view_appbar, this)

        elevation = DEFAULT_ELEVATION

        setColorScheme(ColorScheme.WAQTI_DEFAULT)

        //roundedCorners()
    }

    inline fun leftImageBack() {
        leftImageView {
            leftImageView.visibility = View.VISIBLE
            leftImageDrawable = context.getDrawable(R.drawable.back_icon)
            setOnClickListener {
                @FragmentNavigation(from = ANY_FRAGMENT, to = ANY_FRAGMENT)
                mainActivity.supportFragmentManager.popBackStack()
            }
        }
    }

    fun resolveLeftImage() {
        if (leftImageView.isVisible) {
            when (leftImage) {
                R.drawable.menu_icon -> leftImageView.setOnClickListener {
                    mainActivity.drawerLayout.openDrawer(GravityCompat.START)
                }
                R.drawable.back_icon -> leftImageView.setOnClickListener {
                    @FragmentNavigation(from = ANY_FRAGMENT, to = ANY_FRAGMENT)
                    mainActivity.supportFragmentManager.popBackStack()
                }
            }
        }
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        backgroundColor = colorScheme.main.toAndroidColor
        leftImageDrawable?.setTint(colorScheme.text.toAndroidColor)
        editTextView.textColor = colorScheme.text.toAndroidColor
        editTextView.hintTextColor = colorScheme.text.withTransparency("7F").toAndroidColor
        rightImageDrawable?.setTint(colorScheme.text.toAndroidColor)
    }

    private inline fun roundedCorners() {
        materialShapeDrawable.apply {
            shapeAppearanceModel = ShapeAppearanceModel.Builder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, context.convertDpToPx(16))
                    .setBottomRightCorner(CornerFamily.ROUNDED, context.convertDpToPx(16))
                    .build()
        }
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(Color.TRANSPARENT)
        background = materialShapeDrawable.apply {
            setTint(color)
            elevation = DEFAULT_ELEVATION
        }
    }

}