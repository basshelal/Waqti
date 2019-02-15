package uk.whitecrescent.waqti.frontend.customview.dialogs

import kotlinx.android.synthetic.main.dialog_colorpicker.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.ColorPickerAdapter

class ColorPickerDialog : MaterialDialog() {

    override val contentView = R.layout.dialog_colorpicker
    var title = "Pick Color"
    var onConfirm: (WaqtiColor) -> Unit = { }
    var onClick: (WaqtiColor) -> Unit = { }
    var initialColor: WaqtiColor = WaqtiColor.DEFAULT
    var pickedColor: WaqtiColor = WaqtiColor.DEFAULT

    override fun onResume() {
        super.onResume()

        dialog.apply {
            dialogTitle_textView.text = title

            colorPicker.apply {
                adapter = ColorPickerAdapter(onClick, initialColor)
            }

            confirm_button.setOnClickListener {
                val color = (colorPicker?.adapter as? ColorPickerAdapter)?.color
                        ?: WaqtiColor.DEFAULT
                pickedColor = color
                onConfirm(color)
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }

}