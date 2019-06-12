package uk.whitecrescent.waqti.frontend.fragments.other

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.edit
import kotlinx.android.synthetic.main.fragment_settings.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.TASK_LIST_WIDTH_KEY
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiFragment

class SettingsFragment : WaqtiFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity.resetNavBarStatusBarColor()

        setUpViews()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews() {
        taskListWidthSetting_seekBar.apply {
            progress = mainActivity.waqtiSharedPreferences.getInt(TASK_LIST_WIDTH_KEY, 70)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + progress

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mainActivity.waqtiSharedPreferences.edit {
                        putInt(TASK_LIST_WIDTH_KEY, progress)
                    }
                }
            })
        }
    }

    override fun finish() {

    }

}