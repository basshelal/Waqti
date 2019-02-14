package uk.whitecrescent.waqti.android.customview.dialogs

import kotlinx.android.synthetic.main.dialog_colorpicker_material.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.ColorPickerAdapter

class MaterialColorPickerDialog : WaqtiMaterialDialog() {

    override val contentView = R.layout.dialog_colorpicker_material
    var title = "Pick Color"
    var onConfirm: (String) -> Unit = { }
    var onClick: (String) -> Unit = { }
    var pickedColor: String = "#FFFFFF"

    override fun onResume() {
        super.onResume()

        dialog.apply {
            dialogTitle_textView.text = title

            colorPicker.apply {
                adapter = ColorPickerAdapter(onClick)
            }

            confirm_button.setOnClickListener {
                onConfirm("")
            }

            cancel_button.setOnClickListener(onCancel)
        }
    }

}