package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.color_circle.view.*
import org.jetbrains.anko.image
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.MaterialColor
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor

class PagedColorPicker
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null) : ViewPager(context, attributeSet)

/*class PagedColorPickerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {

    }

    override fun getCount(): Int {

    }


}*/

class ColorPicker
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    init {
        layoutManager = GridLayoutManager(context, 5, VERTICAL, false)
    }

}

class ColorPickerAdapter(val onClick: (WaqtiColor) -> Unit,
                         var color: WaqtiColor = WaqtiColor.DEFAULT)
    : RecyclerView.Adapter<ColorViewHolder>() {

    private val colors = MaterialColor.getAllMaterialColors()
    private var currentChecked: ColorViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.color_circle, parent, false))
    }

    override fun getItemCount() = colors.size

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.image.apply {
            fun check() {
                color = colors[position]
                setImageResource(R.drawable.done_icon)
                background = color.toColorDrawable
            }

            image = colors[position].toColorDrawable

            if (colors[position] == color) {
                check()
                if (currentChecked == null) currentChecked = holder
            }

            setOnClickListener {
                check()

                if (currentChecked != null && currentChecked != holder) {
                    currentChecked?.image?.apply {
                        setImageDrawable(null)
                    }
                }

                currentChecked = holder

                onClick(color)
            }
        }
        holder.name.apply {
            text = colors[position].value
        }
    }

}

class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView
        get() = itemView.circle_imageView
    val name: TextView
        get() = itemView.colorName_textView
}