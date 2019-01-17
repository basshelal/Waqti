package uk.whitecrescent.waqti.android.customview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.MainActivityViewModel

abstract class WaqtiMaterialDialog : BottomSheetDialogFragment() {

    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MainActivityViewModel

    lateinit var dialog: BottomSheetDialog
    abstract val contentView: Int
    open var onCancel: View.OnClickListener = View.OnClickListener {
        this.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(contentView)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        viewModel = mainActivity.viewModel
    }
}