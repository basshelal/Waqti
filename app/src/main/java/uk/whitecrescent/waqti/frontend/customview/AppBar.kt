@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.image
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.ANY_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity

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
) : FrameLayout(context, attributeSet, defStyle), StatefulView<AppBar.State> {

    companion object {
        val DEFAULT_ELEVATION = 16F
    }

    override val state = State()

    val default = 16F

    private val materialShapeDrawable = MaterialShapeDrawable(context, attributeSet, defStyle, 0)

    inline val leftImageView: ImageView get() = appBar_leftImageView
    inline val rightImageView: ImageView get() = appBar_rightImageView
    inline val editTextView: EditTextView get() = appBar_editTextView

    inline var leftImage: Drawable?
        set(value) = leftImageView.setImageDrawable(value)
        get() = leftImageView.drawable

    inline var rightImage: Drawable?
        set(value) = rightImageView.setImageDrawable(value)
        get() = rightImageView.drawable

    var colorScheme: ColorScheme = ColorScheme.WAQTI_DEFAULT
        set(value) {
            field = value
            backgroundColor = colorScheme.main.toAndroidColor
            leftImage?.setTint(colorScheme.text.toAndroidColor)
            editTextView.updateState { textColor = colorScheme.text }
            rightImage?.setTint(colorScheme.text.toAndroidColor)
        }

    init {
        View.inflate(context, R.layout.view_appbar, this)

        updateUI()
    }

    inline fun leftImageMenu() {
        leftImageView {
            leftImageView.visibility = View.VISIBLE
            leftImage = context.getDrawable(R.drawable.menu_icon)
            setOnClickListener {
                mainActivity.drawerLayout.apply {
                    openDrawer(GravityCompat.START)
                }
            }
        }
    }

    inline fun leftImageBack() {
        leftImageView {
            leftImageView.visibility = View.VISIBLE
            leftImage = context.getDrawable(R.drawable.back_icon)
            setOnClickListener {
                @FragmentNavigation(from = ANY_FRAGMENT, to = ANY_FRAGMENT)
                mainActivity.supportFragmentManager.popBackStack()
            }
        }
    }

    inline fun rightImageOptions() {
        rightImageView {
            rightImageView.visibility = View.VISIBLE
            rightImage = context.getDrawable(R.drawable.overflow_icon)
        }
    }

    override inline fun updateState(apply: State.() -> Unit): AppBar {
        state.reset().apply(apply)
        return updateUI()
    }

    override inline fun updateUI(): AppBar {
        with(state) {
            leftImageView.image = if (leftImage != Int.MIN_VALUE) context.getDrawable(leftImage) else null
            rightImageView.image = if (rightImage != Int.MIN_VALUE) context.getDrawable(rightImage) else null
            leftImageView.isVisible = leftImageVisible
            rightImageView.isVisible = rightImageVisible
            this@AppBar.elevation = elevation
            this@AppBar.colorScheme = colorScheme
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
        return this
    }

    override fun setBackgroundColor(color: Int) {
        background = materialShapeDrawable.apply { setTint(color) }
    }

    inner class State : StatefulView.ViewState() {
        var leftImage: Int = Int.MIN_VALUE
        var rightImage: Int = Int.MIN_VALUE
        var leftImageVisible: Boolean = true
        var rightImageVisible: Boolean = true
        var elevation: Float = default
        var colorScheme: ColorScheme = ColorScheme.WAQTI_DEFAULT

        fun reset(): State {
            leftImage = Int.MIN_VALUE
            rightImage = Int.MIN_VALUE
            leftImageVisible = true
            rightImageVisible = true
            elevation = default
            colorScheme = ColorScheme.WAQTI_DEFAULT
            return this
        }
    }

}