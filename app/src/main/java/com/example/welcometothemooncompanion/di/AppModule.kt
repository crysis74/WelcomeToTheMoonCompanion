package com.example.welcometothemooncompanion.di

import android.content.res.AssetManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.welcometothemooncompanion.decktools.DeckGenerator
import com.example.welcometothemooncompanion.repository.CardRepository
import com.example.welcometothemooncompanion.repository.GameSettingRepository
import com.example.welcometothemooncompanion.ui.game.GameViewModel
import com.example.welcometothemooncompanion.ui.gamecreation.GameCreationViewModel
import kotlinx.coroutines.MainScope
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single<Json> { Json }
    single<AssetManager> { androidContext().assets }
    viewModelOf(::GameViewModel)
    singleOf(::CardRepository) {
        createdAtStart()
    }
    singleOf(::DeckGenerator)
    single {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("user_preferences")
        }
    }
    singleOf(::GameSettingRepository)
    single { MainScope() }
    viewModelOf(::GameCreationViewModel)
}