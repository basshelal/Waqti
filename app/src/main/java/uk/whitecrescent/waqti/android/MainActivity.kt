package uk.whitecrescent.waqti.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.views.HomeFragment
import uk.whitecrescent.waqti.model.persistence.Caches

// We want to have a Single Activity Application :)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.blank_constraintLayout, HomeFragment.newInstance(), "Home")
            commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Caches.close()
    }
}
