@file:Suppress("NOTHING_TO_INLINE")

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
import uk.whitecrescent.waqti.frontend.TASK_CARD_TEXT_SIZE
import uk.whitecrescent.waqti.frontend.TASK_LIST_WIDTH_KEY
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiFragment
import uk.whitecrescent.waqti.getPercent
import uk.whitecrescent.waqti.getValue
import uk.whitecrescent.waqti.mainActivity

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
    private inline fun setUpViews() {
        taskListWidthSetting_seekBar.apply {

            val range = 20 to 80

            val fromPreferences = mainActivity.waqtiSharedPreferences.getInt(TASK_LIST_WIDTH_KEY, 66)

            progress = range.getPercent(fromPreferences)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + fromPreferences

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + range.getValue(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mainActivity.waqtiSharedPreferences.edit {
                        putInt(TASK_LIST_WIDTH_KEY, range.getValue(progress))
                    }
                }
            })
        }

        cardTextSizeSetting_seekBar.apply {

            val range = 14 to 30

            val fromPreferences = mainActivity.waqtiSharedPreferences.getInt(TASK_CARD_TEXT_SIZE, 18)

            progress = range.getPercent(fromPreferences)

            cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) +
                    fromPreferences

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) +
                            range.getValue(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mainActivity.waqtiSharedPreferences.edit {
                        putInt(TASK_CARD_TEXT_SIZE, range.getValue(progress))
                    }
                }
            })
        }

        resetToDefaults_button.apply {
            setOnClickListener {
                ConfirmDialog().apply {
                    title = this@SettingsFragment.mainActivity
                            .getString(R.string.resetToDefaults)
                    message = this@SettingsFragment.mainActivity
                            .getString(R.string.resetToDefaultsQuestion)
                    onConfirm = {
                        resetSettingsToDefaults()
                        this.dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private inline fun resetSettingsToDefaults() {
        taskListWidthSetting_seekBar.apply {

            val range = 20 to 100

            val default = 66

            progress = range.getPercent(default)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + default

            mainActivity.waqtiSharedPreferences.edit {
                putInt(TASK_LIST_WIDTH_KEY, range.getValue(progress))
            }
        }

        cardTextSizeSetting_seekBar.apply {

            val range = 14 to 30

            val default = 18

            progress = range.getPercent(default)
            cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) + default

            mainActivity.waqtiSharedPreferences.edit {
                putInt(TASK_CARD_TEXT_SIZE, range.getValue(progress))
            }
        }
    }

    override fun finish() {

    }

}