package uk.whitecrescent.waqti.frontend.customview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uk.whitecrescent.waqti.frontend.MainActivity

/**
 * Parent of all Dialogs in Waqti.
 * Basically this is just a [BottomSheetDialogFragment] that extends fully when initialized and
 * has some commonly used behaviours already given, such as [mainActivity].
 *
 * Just provide the layout id by overriding [contentView] with the layout,
 * eg `R.layout.dialog_confirm`.
 *
 * Most MaterialDialogs in Waqti should have a confirm and cancel button and should have
 * callbacks in them so that the taken action can be determined at call site.
 */
abstract class MaterialDialog : BottomSheetDialogFragment() {

    abstract val contentView: Int
    lateinit var mainActivity: MainActivity
    lateinit var dialog: BottomSheetDialog
    lateinit var bottomSheet: FrameLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    open var onCancel: View.OnClickListener = View.OnClickListener {
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(contentView)
        dialog.setOnShowListener {
            bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)!!
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
    }
}