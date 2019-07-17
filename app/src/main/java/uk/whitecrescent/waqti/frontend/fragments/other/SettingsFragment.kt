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
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiFragment
import uk.whitecrescent.waqti.getPercent
import uk.whitecrescent.waqti.getValue
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity

class SettingsFragment : WaqtiFragment() {

    private val taskListWidthRange = 20 to 80
    private val cardTextSizeRange = 14 to 30

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpViews()
    }

    @SuppressLint("SetTextI18n")
    override fun setUpViews() {
        setUpAppBar()

        taskListWidthSetting_seekBar {

            val fromPreferences = mainActivity.waqtiPreferences.taskListWidth

            progress = taskListWidthRange.getPercent(fromPreferences)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + fromPreferences

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    taskListWidthSetting_textView.text = getString(R.string.taskListWidth) +
                            taskListWidthRange.getValue(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mainActivity.waqtiPreferences.taskListWidth = taskListWidthRange.getValue(progress)
                    mainActivityVM.settingsChanged = true
                }
            })
        }

        cardTextSizeSetting_seekBar {

            val fromPreferences = mainActivity.waqtiPreferences.taskCardTextSize

            progress = cardTextSizeRange.getPercent(fromPreferences)

            cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) +
                    fromPreferences

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) +
                            cardTextSizeRange.getValue(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mainActivity.waqtiPreferences.taskCardTextSize = cardTextSizeRange.getValue(progress)
                    mainActivityVM.settingsChanged = true
                }
            })
        }

        boardScrollSnapMode_spinner {
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
                    mainActivityVM.settingsChanged = true
                }
            }
        }

        resetToDefaults_button {
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

    override fun setUpAppBar() {
        mainActivity.appBar {
            elevation = DEFAULT_ELEVATION
            leftImageBack()
            editTextView {
                textColor = WaqtiColor.WAQTI_WHITE.toAndroidColor
                removeAllTextChangedListeners()
                isEditable = false
                text = SpannableStringBuilder(getString(R.string.settings))
            }
            rightImageView.isInvisible = true
        }
        mainActivity.resetColorScheme()
    }

    @SuppressLint("SetTextI18n")
    private inline fun resetSettingsToDefaults() {
        taskListWidthSetting_seekBar {

            val default = 66

            progress = taskListWidthRange.getPercent(default)
            taskListWidthSetting_textView.text = getString(R.string.taskListWidth) + default

            mainActivity.waqtiPreferences.taskListWidth = taskListWidthRange.getValue(progress)
            mainActivityVM.settingsChanged = true
        }

        cardTextSizeSetting_seekBar {

            val default = 18

            progress = cardTextSizeRange.getPercent(default)
            cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) + default

            mainActivity.waqtiPreferences.taskCardTextSize = cardTextSizeRange.getValue(progress)
            mainActivityVM.settingsChanged = true
        }

        boardScrollSnapMode_spinner {
            setSelection(ScrollSnapMode.PAGED.ordinal, true)

            mainActivity.waqtiPreferences.boardScrollSnapMode = ScrollSnapMode.PAGED
            mainActivityVM.settingsChanged = true
        }
    }

    override fun finish() {

    }

}