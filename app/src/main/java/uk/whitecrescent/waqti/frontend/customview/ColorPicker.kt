package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.color_circle.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.MaterialColorLevel
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.appearance.getMaterialColors

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

    private val colors = getMaterialColors(MaterialColorLevel.TWO_HUNDRED).toList()
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

            setImageDrawable(colors[position].toColorDrawable)

            if (currentChecked == null && colors[position] == color) {
                check()
                currentChecked = holder
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
    }

}

class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView
        get() = itemView.circle_imageView
}