package uk.whitecrescent.waqti.android.customview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_confirm_material.*
import uk.whitecrescent.waqti.R

class MaterialConfirmDialog : WaqtiMaterialDialog() {

    lateinit var dialog: BottomSheetDialog

    var title = "Confirm Action"
    var message = ""

    var onConfirm: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {

        }
    }

    var onCancel: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            this@MaterialConfirmDialog.dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(R.layout.dialog_confirm_material)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onResume() {
        super.onResume()

        dialog.apply {

            if (message == "") dialogMessage_textView.visibility = View.GONE
            else dialogMessage_textView.text = message

            dialogTitle_textView.text = title

            confirm_button.setOnClickListener(onConfirm)

            cancel_button.setOnClickListener(onCancel)
        }
    }

}