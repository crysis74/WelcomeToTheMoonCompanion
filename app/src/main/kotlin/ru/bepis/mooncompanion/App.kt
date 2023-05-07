package ru.bepis.mooncompanion

import android.app.Application
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.bepis.mooncompanion.di.appModule

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
