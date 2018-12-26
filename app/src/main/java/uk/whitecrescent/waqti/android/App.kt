package uk.whitecrescent.waqti.android

import android.app.Application
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import com.jakewharton.threetenabp.AndroidThreeTen
import uk.whitecrescent.waqti.model.now

//import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        // TODO: 26-Dec-18 About 800 ms between here at first line of Activity's onCreate
        // I suspect its the Caches.initialize, I hope we can figure out some sort of
        // asynchronous way to initialize it but I'm not sure

        Database.build(applicationContext)
        Caches.initialize()
    }
}