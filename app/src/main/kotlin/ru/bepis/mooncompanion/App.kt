package ru.bepis.mooncompanion

import android.app.Application
import ru.bepis.mooncompanion.di.appModule
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        loadKoin()
    }

    private fun loadKoin() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
