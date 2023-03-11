package com.example.welcometothemooncompanion

import android.app.Application
import com.example.welcometothemooncompanion.di.appModule
import com.example.welcometothemooncompanion.repository.CardRepository
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val gameCreationRepository: CardRepository by inject()

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        loadKoin()
        gameCreationRepository.init()
    }

    private fun loadKoin() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
