package uk.whitecrescent.waqti.android

import android.app.Application
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import com.jakewharton.threetenabp.AndroidThreeTen
import uk.whitecrescent.waqti.model.now

//import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    // Ignore the error in AndroidThreeTen.init() it doesn't fail the build and runs fine on
    // emulator and physical, I don't know how to get rid of the error, but it's wrong
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Database.build(this)
        Caches.initialize()
    }
}