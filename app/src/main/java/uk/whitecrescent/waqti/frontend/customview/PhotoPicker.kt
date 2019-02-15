package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
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
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.DEFAULT_PHOTO
import uk.whitecrescent.waqti.keys.UNSPLASH_ACCESS_KEY
import uk.whitecrescent.waqti.logE

class PhotoPicker
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    init {
        layoutManager = GridLayoutManager(context, 2, VERTICAL, false)
    }

}

class PhotoPickerAdapter(val onClick: (Photo) -> Unit,
                         var photo: Photo = DEFAULT_PHOTO)
    : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.unsplash_image, parent, false))
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (!holder.hasImage) {
            Unsplash(UNSPLASH_ACCESS_KEY).getRandomPhoto(
                    null, null, null, null, null, null, null,
                    object : Unsplash.OnPhotoLoadedListener {
                        override fun onComplete(photo: Photo?) {
                            if (!holder.hasImage) {
                                Glide.with(holder.image)
                                        .load(photo?.urls?.small)
                                        .into(holder.image)
                                holder.hasImage = true
                            }
                        }

                        override fun onError(error: String?) {
                            logE(error)
                        }

                    }
            )
        }


    }

}

class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var hasImage = false
    val image: ImageView
        get() = itemView.imageView
}