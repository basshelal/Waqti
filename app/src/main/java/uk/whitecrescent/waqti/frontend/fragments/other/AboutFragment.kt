@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.other

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.fragment_about.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.colorAttr
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.ABOUT_FRAGMENT
import uk.whitecrescent.waqti.frontend.ANY_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.WAQTI_VERSION
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiFragment
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.toEditable

class AboutFragment : WaqtiFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViews()
    }

    override fun setUpViews() {
        setUpAppBar()
        val view = AboutPage(mainActivity)
                .isRTL(false)

                // Image and Description
                .setImage(R.drawable.waqti_icon)
                .setDescription(getString(R.string.waqtiDescription) +
                        "\n\n" + getString(R.string.waqtiBetaText))

                // Waqti is free and open source
                .addHeading(getString(R.string.waqtiOpenSource))
                .addGitHubExt("basshelal/Waqti-Android", "Waqti on GitHub")
                .addLicense()

                // Open Source Libraries Used
                .addHeading(getString(R.string.openSourceLibraries))
                .addLibrary("JetBrains/kotlin", "Kotlin")
                .addLibrary("objectbox/objectbox-java", "ObjectBox")
                .addLibrary("ReactiveX/RxJava", "RxJava")
                .addLibrary("ReactiveX/RxAndroid", "RxAndroid")
                .addLibrary("junit-team/junit5", "JUnit5")
                .addLibrary("JakeWharton/ThreeTenABP", "ThreeTenABP")
                .addLibrary("google/gson", "Gson")
                .addLibrary("material-components/material-components-android", "Material Components for Android")
                .addLibrary("florent37/ShapeOfView", "ShapeOfView")
                .addLibrary("Kotlin/anko", "Anko")
                .addLibrary("square/leakcanary", "LeakCanary")
                .addLibrary("KeenenCharles/AndroidUnplash", "Android Unsplash")
                .addLibrary("lopspower/CircularImageView", "CircularImageView")
                .addLibrary("square/retrofit", "Retrofit")
                .addLibrary("bumptech/glide", "Glide")
                .addLibrary("medyo/android-about-page", "Android About Page")
                .addLibrary("afollestad/material-dialogs", "Material Dialogs")
                .addLibrary("aritraroy/Flashbar", "Flashbar")
                .addLibrary("TakuSemba/Spotlight", "Spotlight")
                .addLibrary("shiburagi/Drawer-Behavior", "Drawer Behavior")
                .addLibrary("arcadefire/nice-spinner", "Nice Spinner")
                .addLibrary("warkiz/IndicatorSeekBar", "IndicatorSeekBar")
                .addLibrary("basshelal/UnsplashPhotoPicker", "Unsplash Photo Picker")

                // About the Developer
                .addHeading(getString(R.string.aboutTheDeveloper))
                .addGitHubExt("basshelal", "Bassam Helal on GitHub")
                .addLinkedIn("bassamhelal", "Bassam Helal on LinkedIn")
                .create()
        about_linearLayout.addView(view)
        about_linearLayout.addView(TextView(context).also {
            it.text = WAQTI_VERSION
            it.textColor = context!!.colorAttr(R.attr.colorOnSurface)
            it.textAlignment = TEXT_ALIGNMENT_CENTER
        })
        about_linearLayout.childrenRecursiveSequence().forEach {
            val color = context!!.colorAttr(R.attr.colorSurface)
            it.backgroundColor = color
            if (it is TextView) it.textColor = context!!.colorAttr(R.attr.colorOnSurface)
        }
    }

    override fun setUpAppBar() {
        mainActivity.appBar {
            elevation = default
            leftImageBack()
            editTextView {
                textColor = WaqtiColor.WAQTI_WHITE.toAndroidColor
                textChangedListener = {}
                isEditable = false
                text = getString(R.string.about).toEditable()
            }
            rightImageView.isInvisible = true
        }
        mainActivity.resetColorScheme()
    }

    override fun finish() {

    }

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = ANY_FRAGMENT, to = ABOUT_FRAGMENT)
                replace(R.id.fragmentContainer, AboutFragment(), ABOUT_FRAGMENT)
                addToBackStack(null)
            }
        }
    }

}

inline fun AboutPage.addHeading(title: String) = addGroup(title)

inline fun AboutPage.addLicense(): AboutPage {
    return addItem(Element().apply {
        this.title = "MIT License"
        iconDrawable = R.drawable.opensource_icon
        iconTint = R.color.primaryColor
        iconNightTint = R.color.primaryColor
        value = "https://github.com/basshelal/Waqti-Android/blob/master/LICENSE"
        intent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse("https://github.com/basshelal/Waqti-Android/blob/master/LICENSE")
        }
    })
}

inline fun AboutPage.addGitHubExt(id: String, title: String): AboutPage {
    return addItem(Element().apply {
        this.title = title
        iconDrawable = mehdi.sakout.aboutpage.R.drawable.about_icon_github
        iconTint = R.color.primaryColor
        iconNightTint = R.color.primaryColor
        value = id
        intent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse(String.format("https://github.com/%s", id))
        }
    })
}

inline fun AboutPage.addLibrary(id: String, title: String) = addGitHubExt(id, title)

inline fun AboutPage.addLinkedIn(id: String, title: String): AboutPage {
    return addItem(Element().apply {
        this.title = title
        iconDrawable = R.drawable.linkedin_icon
        iconTint = R.color.primaryColor
        iconNightTint = R.color.primaryColor
        value = id
        intent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse(String.format("https://linkedin.com/in/%s", id))
        }
    })
}