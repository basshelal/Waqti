@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.other

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.fragment_settings.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode
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

        setUpViews()
    }

    @SuppressLint("SetTextI18n")
    override fun setUpViews() {
        mainActivity.appBar {
            color = WaqtiColor.WAQTI_DEFAULT
            elevation = DEFAULT_ELEVATION
            leftImageDefault()
            editTextView {
                removeAllTextChangedListeners()
                isEditable = false
                text = SpannableStringBuilder(getString(R.string.settings))
            }
            rightImageView.isInvisible = true
        }
        mainActivity.resetNavBarStatusBarColor()

        taskListWidthSetting_seekBar.apply {

            val range = 20 to 80

            val fromPreferences = mainActivity.waqtiPreferences.taskListWidth

            progress = range.getPercent(fromPreferences)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + fromPreferences

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + range.getValue(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mainActivity.waqtiPreferences.taskListWidth = range.getValue(progress)
                }
            })
        }

        cardTextSizeSetting_seekBar.apply {

            val range = 14 to 30

            val fromPreferences = mainActivity.waqtiPreferences.taskCardTextSize

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
                    mainActivity.waqtiPreferences.taskCardTextSize = range.getValue(progress)
                }
            })
        }

        boardScrollSnapMode_spinner.apply {
            adapter = ArrayAdapter.createFromResource(
                    mainActivity,
                    R.array.boardScrollSnapModeOptions,
                    R.layout.support_simple_spinner_dropdown_item
            ).also {
                it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }
            setSelection(mainActivity.waqtiPreferences.boardScrollSnapMode.ordinal, true)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                            id: Long) {
                    mainActivity.waqtiPreferences.boardScrollSnapMode =
                            ScrollSnapMode.valueOf(selectedItem.toString().toUpperCase())
                }
            }
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

            val range = 20 to 80

            val default = 66

            progress = range.getPercent(default)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + default

            mainActivity.waqtiPreferences.taskListWidth = range.getValue(progress)
        }

        cardTextSizeSetting_seekBar.apply {

            val range = 14 to 30

            val default = 18

            progress = range.getPercent(default)
            cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) + default

            mainActivity.waqtiPreferences.taskCardTextSize = range.getValue(progress)
        }

        boardScrollSnapMode_spinner.apply {
            setSelection(ScrollSnapMode.PAGED.ordinal, true)

            mainActivity.waqtiPreferences.boardScrollSnapMode = ScrollSnapMode.PAGED
        }
    }

    override fun finish() {

    }

}