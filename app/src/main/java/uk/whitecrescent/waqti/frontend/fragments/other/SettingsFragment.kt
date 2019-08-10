@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.other

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.fragment_settings.*
import org.angmarch.views.OnSpinnerItemSelectedListener
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.ANY_FRAGMENT
import uk.whitecrescent.waqti.frontend.AppTheme
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.SETTINGS_FRAGMENT
import uk.whitecrescent.waqti.frontend.WaqtiPreferences
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiFragment
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.onSeek

class SettingsFragment : WaqtiFragment() {

    private inline val preferences: WaqtiPreferences
        get() = mainActivity.preferences

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

        appTheme_spinner {
            selectedIndex = preferences.appTheme.ordinal
            onSpinnerItemSelectedListener = OnSpinnerItemSelectedListener { parent, view, position, id ->
                mainActivity.setTheme(AppTheme.valueOf(selectedItem.toString().toUpperCase()))
            }
        }

        listWidthSetting_seekBar {

            val fromPrefs = preferences.listWidth

            setProgress(fromPrefs.toFloat())

            listWidthSetting_textView.text = getString(R.string.taskListWidth) + " " + fromPrefs

            onSeek {
                listWidthSetting_textView.text = getString(R.string.taskListWidth) + " " + progress
                preferences.listWidth = progress
            }
        }

        cardTextSizeSetting_seekBar {

            val fromPrefs = preferences.cardTextSize

            setProgress(fromPrefs.toFloat())

            cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) + " " + fromPrefs

            onSeek {
                cardTextSizeSetting_textView.text = getString(R.string.taskCardTextSize) + " " + progress
                preferences.cardTextSize = progress
            }
        }

        listHeaderTextSizeSetting_seekBar {

            val fromPrefs = preferences.listHeaderTextSize

            setProgress(fromPrefs.toFloat())

            headerTextSizeSetting_textView.text = getString(R.string.listHeaderTextSize) + " " + fromPrefs

            onSeek {
                headerTextSizeSetting_textView.text = getString(R.string.listHeaderTextSize) + " " + progress
                preferences.listHeaderTextSize = progress
            }
        }

        boardScrollSnapMode_spinner {
            selectedIndex = preferences.boardScrollSnapMode.ordinal
            onSpinnerItemSelectedListener = OnSpinnerItemSelectedListener { parent, view, position, id ->
                preferences.boardScrollSnapMode = ScrollSnapMode.valueOf(selectedItem.toString().toUpperCase())
            }
        }

        changeNavBarColor_checkBox {
            isChecked = preferences.changeNavBarColor
            setOnCheckedChangeListener { _, isChecked ->
                preferences.changeNavBarColor = isChecked
                mainActivity.resetColorScheme()
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
                textChangedListener = {}
                isEditable = false
                text = SpannableStringBuilder(getString(R.string.settings))
            }
            rightImageView.isInvisible = true
        }
        mainActivity.resetColorScheme()
    }

    private inline fun resetSettingsToDefaults() {
        appTheme_spinner {
            selectedIndex = AppTheme.LIGHT.ordinal
            mainActivity.setTheme(AppTheme.LIGHT)
        }

        listWidthSetting_seekBar { setProgress(66F) }

        cardTextSizeSetting_seekBar { setProgress(18F) }

        listHeaderTextSizeSetting_seekBar { setProgress(28F) }

        changeNavBarColor_checkBox { isChecked = true }

        boardScrollSnapMode_spinner {
            selectedIndex = ScrollSnapMode.PAGED.ordinal
            preferences.boardScrollSnapMode = ScrollSnapMode.PAGED
        }
    }

    override fun finish() {

    }

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = ANY_FRAGMENT, to = SETTINGS_FRAGMENT)
                replace(R.id.fragmentContainer, SettingsFragment(), SETTINGS_FRAGMENT)
                addToBackStack(null)

            }
        }
    }

}