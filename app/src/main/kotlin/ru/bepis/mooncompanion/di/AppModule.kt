package ru.bepis.mooncompanion.di

import android.content.res.AssetManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.MainScope
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.bepis.mooncompanion.decktools.DeckGenerator
import ru.bepis.mooncompanion.decktools.MissionsGenerator
import ru.bepis.mooncompanion.repository.CardRepository
import ru.bepis.mooncompanion.repository.GameSettingRepository
import ru.bepis.mooncompanion.repository.MissionImageResGenerator
import ru.bepis.mooncompanion.repository.MissionRepository
import ru.bepis.mooncompanion.ui.game.GameViewModel
import ru.bepis.mooncompanion.ui.gamecreation.GameCreationViewModel
import ru.bepis.mooncompanion.ui.mission.MissionBottomSheetAnimationGenerator
import ru.bepis.mooncompanion.ui.mission.MissionViewModel

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