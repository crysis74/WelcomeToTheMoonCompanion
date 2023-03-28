package com.example.welcometothemooncompanion.di

import android.content.res.AssetManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.welcometothemooncompanion.decktools.DeckGenerator
import com.example.welcometothemooncompanion.decktools.MissionsGenerator
import com.example.welcometothemooncompanion.repository.CardRepository
import com.example.welcometothemooncompanion.repository.GameSettingRepository
import com.example.welcometothemooncompanion.repository.MissionImageResGenerator
import com.example.welcometothemooncompanion.repository.MissionRepository
import com.example.welcometothemooncompanion.ui.game.GameViewModel
import com.example.welcometothemooncompanion.ui.gamecreation.GameCreationViewModel
import com.example.welcometothemooncompanion.ui.mission.MissionBottomSheetAnimationGenerator
import com.example.welcometothemooncompanion.ui.mission.MissionViewModel
import kotlinx.coroutines.MainScope
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val coreModule = module {
    single<Json> { Json }
    single<AssetManager> { androidContext().assets }
    single {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("user_preferences")
        }
    }
    single { MainScope() }
    singleOf(::GameSettingRepository)
}

private val gameCreationModule = module {
    includes(coreModule)
    viewModelOf(::GameCreationViewModel)
}

private val gameModule = module {
    includes(coreModule)
    singleOf(::CardRepository) { createdAtStart() }
    singleOf(::DeckGenerator)
    viewModelOf(::GameViewModel)
}

private val missionModule = module {
    includes(coreModule)
    singleOf(::MissionImageResGenerator)
    singleOf(::MissionRepository)
    singleOf(::MissionsGenerator)
    viewModelOf(::MissionViewModel)
    factoryOf(::MissionBottomSheetAnimationGenerator)
}

val appModule = module {
    includes(
        gameCreationModule,
        gameModule,
        missionModule
    )
}