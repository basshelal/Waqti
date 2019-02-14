package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.objectbox.converter.PropertyConverter
import kotlinx.android.synthetic.main.color_circle.view.*
import uk.whitecrescent.waqti.R

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

    private val colors = COLORS_200.toList()
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

data class WaqtiColor(val value: String) {
    init {
        require(value.startsWith('#')) { "Color must start with #" }
        require(value.length <= 9) { "Color value must be no longer than 9 characters (includes #)" }
    }

    val toColorDrawable: ColorDrawable
        get() = ColorDrawable(Color.parseColor(value))

    val toAndroidColor: Int
        get() = Color.parseColor(value)

    companion object {
        val DEFAULT = WaqtiColor("#FFFFFF")
        val CARD_DEFAULT = WaqtiColor("#E0E0E0")
    }
}

class WaqtiColorConverter : PropertyConverter<WaqtiColor, String> {

    override fun convertToDatabaseValue(entityProperty: WaqtiColor?): String {
        return entityProperty?.value ?: WaqtiColor.DEFAULT.value
    }

    override fun convertToEntityProperty(databaseValue: String?): WaqtiColor {
        return if (databaseValue == null) WaqtiColor.DEFAULT else WaqtiColor(databaseValue)
    }

}

inline val String.toColor: WaqtiColor
    get() = WaqtiColor(this)

val COLORS_100 = listOf(
        "#FFFFFF", "#ffcdd2", "#f8bbd0", "#e1bee7", "#d1c4e9",
        "#c5cae9", "#bbdefb", "#b3e5fc", "#b2ebf2", "#b2dfdb",
        "#c8e6c9", "#dcedc8", "#f0f4c3", "#fff9c4", "#ffecb3",
        "#ffe0b2", "#ffccbc", "#d7ccc8", "#f5f5f5", "#cfd8dc"
).map { it.toColor }

val COLORS_200 = listOf(
        "#FFFFFF", "#ef9a9a", "#f48fb1", "#ce93d8", "#b39ddb",
        "#9fa8da", "#90caf9", "#81d4fa", "#80deea", "#80cbc4",
        "#a5d6a7", "#c5e1a5", "#e6ee9c", "#fff59d", "#ffe082",
        "#ffcc80", "#ffab91", "#bcaaa4", "#eeeeee", "#b0bec5"
).map { it.toColor }