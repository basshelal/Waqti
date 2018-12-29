package uk.whitecrescent.waqti.android.customview.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.MainActivityViewModel

abstract class WaqtiDialog : DialogFragment() {

    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MainActivityViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        viewModel = mainActivity.viewModel
    }
}

abstract class WaqtiMaterialDialog : BottomSheetDialogFragment() {
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MainActivityViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        viewModel = mainActivity.viewModel
    }
}