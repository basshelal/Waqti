package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kc.unsplash.Unsplash
import com.kc.unsplash.models.Photo
import kotlinx.android.synthetic.main.unsplash_image.view.*
import org.jetbrains.anko.image
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.DEFAULT_PHOTO
import kotlin.random.Random

class PhotoPicker
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    init {
        layoutManager = GridLayoutManager(context, 2, VERTICAL, false)
    }

}

class PhotoPickerAdapter(val onClick: (Photo) -> Unit,
                         var photo: Photo = DEFAULT_PHOTO,
                         val __onClick: (Drawable) -> Unit)
    : RecyclerView.Adapter<PhotoViewHolder>() {

    lateinit var recyclerView: RecyclerView
    lateinit var __drawable: Drawable

    override fun onAttachedToRecyclerView(_recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(_recyclerView)
        recyclerView = _recyclerView
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.unsplash_image, parent, false))
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(recyclerView)
                .load(Uri.parse(PICTURES[Random.nextInt(0, PICTURES.size)]))
                .placeholder(R.drawable.waqti_icon)
                .error(R.drawable.delete_icon)
                .into(holder.image)

        holder.image.setOnClickListener {
            __drawable = holder.image.image!!
            __onClick(__drawable)
        }

    }

}

@Suppress("NOTHING_TO_INLINE")
inline fun Unsplash.randomPhoto(listener: Unsplash.OnPhotoLoadedListener) = getRandomPhoto(
        null, null, null, null, null, null, null, listener)

class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView
        get() = itemView.imageView
}

val TALL_PICTURE1 = "https://images.unsplash.com/photo-1548764959-bcab742d6724?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val TALL_PICTURE2 = "https://images.unsplash.com/photo-1549317935-48ecdbd71bd5?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val TALL_PICTURE3 = "https://images.unsplash.com/photo-1548722318-8537fb197868?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val TALL_PICTURE4 = "https://images.unsplash.com/photo-1548282638-266858867ed5?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val WIDE_PICTURE1 = "https://images.unsplash.com/photo-1548617335-c1b176388c65?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val WIDE_PICTURE2 = "https://images.unsplash.com/photo-1548440914-fcd7b0878ca0?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val WIDE_PICTURE3 = "https://images.unsplash.com/photo-1548165036-e241c64aa5b6?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"
val WIDE_PICTURE4 = "https://images.unsplash.com/photo-1549253924-6e94dc79ad0d?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjF9"

val PICTURES = listOf(TALL_PICTURE1, TALL_PICTURE2, TALL_PICTURE3, TALL_PICTURE4,
        WIDE_PICTURE1, WIDE_PICTURE2, WIDE_PICTURE3, WIDE_PICTURE4)