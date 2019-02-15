package uk.whitecrescent.waqti.frontend.customview.dialogs

import com.kc.unsplash.models.Photo
import kotlinx.android.synthetic.main.dialog_photopicker.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.DEFAULT_PHOTO
import uk.whitecrescent.waqti.frontend.customview.PhotoPickerAdapter

class PhotoPickerDialog : MaterialDialog() {

    override val contentView = R.layout.dialog_photopicker
    var title = "Pick Color"
    var onConfirm: (Photo) -> Unit = { }
    var onClick: (Photo) -> Unit = { }
    var initialPhoto: Photo = DEFAULT_PHOTO
    var pickedPhoto: Photo = DEFAULT_PHOTO

    override fun onResume() {
        super.onResume()

        dialog.apply {
            dialogTitle_textView.text = title

            photoPicker.apply {
                adapter = PhotoPickerAdapter(onClick, initialPhoto)
            }

            confirm_button.setOnClickListener {
                val photo = (photoPicker?.adapter as? PhotoPickerAdapter)?.photo
                        ?: DEFAULT_PHOTO
                pickedPhoto = photo
                onConfirm(photo)
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }

}