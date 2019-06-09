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
                        .setImage(R.drawable.waqti_icon)
                        .setDescription(getString(R.string.waqtiDescription))
                        .addParagraph(getString(R.string.waqtiBetaText))
                        .addHeading(getString(R.string.waqtiOpenSource))
                        .addGitHub("basshelal/Waqti-Android", "Waqti on GitHub")
                        .addHeading(getString(R.string.openSourceLibraries))
                        .addGitHub("objectbox/objectbox-java", "ObjectBox")
                        .addHeading("About the Developer")
                        .addGitHub("basshelal", "Bassam Helal on GitHub")
                        .addLinkedIn("bassamhelal", "Bassam Helal on LinkedIn")
                        .create()
        )
    }

    override fun finish() {

    }
}

inline fun AboutPage.addHeading(title: String) = addGroup(title)

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