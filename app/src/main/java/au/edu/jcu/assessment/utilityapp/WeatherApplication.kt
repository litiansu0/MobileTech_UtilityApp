package au.edu.jcu.assessment.utilityapp

import android.app.Application
import au.edu.jcu.assessment.utilityapp.di.AppContainer
import au.edu.jcu.assessment.utilityapp.di.DefaultAppContainer

/** Creates the [AppContainer] once per app process so all screens share the same repositories. */
class WeatherApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
