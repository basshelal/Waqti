@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.other

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiOtherFragment

class AboutFragment : WaqtiOtherFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity.resetNavBarStatusBarColor()

        setUpViews()
    }

    private inline fun setUpViews() {
        about_linearLayout.addView(
                AboutPage(mainActivity)
                        .isRTL(false)

                        // Image and Description
                        .setImage(R.drawable.waqti_icon)
                        .setDescription(getString(R.string.waqtiDescription) +
                                "\n\n" + getString(R.string.waqtiBetaText))
                        //.addParagraph(getString(R.string.waqtiBetaText))

                        // Waqti is free and open source
                        .addHeading(getString(R.string.waqtiOpenSource))
                        .addGitHub("basshelal/Waqti-Android", "Fork Waqti on GitHub")
                        .addLink("MIT License",
                                "https://github.com/basshelal/Waqti-Android/blob/master/LICENSE")

                        // Open Source Libraries Used
                        .addHeading(getString(R.string.openSourceLibraries))
                        .addLibrary("JetBrains/kotlin", "Kotlin")
                        .addLibrary("junit-team/junit5", "JUnit5")
                        .addLibrary("objectbox/objectbox-java", "ObjectBox")
                        .addLibrary("ReactiveX/RxJava", "RxJava")
                        .addLibrary("JakeWharton/ThreeTenABP", "ThreeTenABP")
                        .addLibrary("google/gson", "Gson")
                        .addLibrary("florent37/ShapeOfView", "ShapeOfView")
                        .addLibrary("Kotlin/anko", "Anko")
                        .addLibrary("square/leakcanary", "LeakCanary")
                        .addLibrary("material-components/material-components-android", "Material Components for Android")
                        .addLibrary("lopspower/CircularImageView", "CircularImageView")
                        .addLibrary("square/retrofit", "Retrofit")
                        .addLibrary("bumptech/glide", "Glide")
                        .addLibrary("KeenenCharles/AndroidUnplash", "Android Unsplash")
                        .addLibrary("medyo/android-about-page", "Android About Page")

                        // About the Developer
                        .addHeading(getString(R.string.aboutTheDeveloper))
                        .addGitHub("basshelal", "Bassam Helal on GitHub")
                        .addLinkedIn("bassamhelal", "Bassam Helal on LinkedIn")
                        .create()
        )
    }

    override fun finish() {

    }
}

inline fun AboutPage.addHeading(title: String) = addGroup("$title")

inline fun AboutPage.addLink(title: String, url: String): AboutPage {
    return addItem(Element().apply {
        this.title = title
        iconDrawable = null
        iconTint = null
        value = url
        intent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse(url)
        }
    })
}

inline fun AboutPage.addLibrary(id: String, title: String) = addGitHub(id, title)

inline fun AboutPage.addParagraph(text: String): AboutPage {
    return addItem(Element().apply {
        title = text
        value = text
    })
}

inline fun AboutPage.addLinkedIn(id: String, title: String): AboutPage {
    return addItem(Element().apply {
        this.title = title
        iconDrawable = R.drawable.linkedin_icon
        iconTint = R.color.linkedInColor
        value = id
        intent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse(String.format("https://linkedin.com/in/%s", id))
        }
    })
}