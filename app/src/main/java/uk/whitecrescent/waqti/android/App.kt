package uk.whitecrescent.waqti.android

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import uk.whitecrescent.waqti.model.persistence.Database

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Database.build(this)
    }
}