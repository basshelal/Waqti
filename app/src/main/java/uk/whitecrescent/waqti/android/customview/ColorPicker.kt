package uk.whitecrescent.waqti.android.customview

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
import uk.whitecrescent.waqti.colorDrawable

class ColorPicker
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    init {
        layoutManager = GridLayoutManager(context, 5, VERTICAL, false)
    }

}

class ColorPickerAdapter(val onClick: (String) -> Unit) : RecyclerView.Adapter<ColorViewHolder>() {

    val colors = COLORS.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.color_circle, parent, false))
    }

    override fun getItemCount() = colors.size

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.image.setImageDrawable(colorDrawable(colors[position]))
        holder.image.setOnClickListener {
            onClick(colors[position])
        }
    }

}

class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView
        get() = itemView.circle_imageView
}

// For now just random colors
val COLORS = arrayOf<String>(
        "#FFFFFF", "#000000", "#880E4F", "#1565C0", "#FFCC33",
        "#660000", "#FF0066", "#333333", "#00CC00", "#663333",
        "#339933", "#660000", "#FFCC66", "#999999", "#FF9900",
        "#99CC66", "#9933FF", "#FF99FF", "#00FF99", "#FFCC66"
)