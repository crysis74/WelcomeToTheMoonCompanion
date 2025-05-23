package ru.bepis.mooncompanion.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.bepis.mooncompanion.domain.ScreenType
import ru.bepis.mooncompanion.repository.GameSettingRepository.PreferencesKey.IS_DEFAULT_SCREEN_TYPE
import ru.bepis.mooncompanion.repository.GameSettingRepository.PreferencesKey.SELECTED_GAME_FIELD
import ru.bepis.mooncompanion.repository.GameSettingRepository.PreferencesKey.SHOULD_SHOW_NEXT_CARD_TYPE

class GameSettingRepository(
    private val dataStore: DataStore<Preferences>,
    private val appScope: CoroutineScope
) {

    val screenType: Flow<ScreenType> = dataStore.safetyData.map {
        fun Boolean.toScreenType() = if (this) ScreenType.Default else ScreenType.Mirrored
        it.isDefaultScreenType.toScreenType()
    }

    val selectedGameField: Flow<Int?> = dataStore.safetyData.map {
        it.selectedGameField
    }

    val shouldShowNextCardType: Flow<Boolean> = dataStore.safetyData.map {
        it.shouldShowNextCardType
    }

    fun toggleScreenType() {
        appScope.launch {
            dataStore.edit {
                it[IS_DEFAULT_SCREEN_TYPE] = !it.isDefaultScreenType
            }
        }
    }

    fun saveGameField(gameField: Int) {
        appScope.launch {
            dataStore.edit {
                it[SELECTED_GAME_FIELD] = gameField
            }
        }
    }

    fun saveShouldShowNextCardType(shouldShow: Boolean) {
        appScope.launch {
            dataStore.edit {
                it[SHOULD_SHOW_NEXT_CARD_TYPE] = shouldShow
            }
        }
    }

    private object PreferencesKey {
        val IS_DEFAULT_SCREEN_TYPE = booleanPreferencesKey("is_default_screen_type")
        val SELECTED_GAME_FIELD = intPreferencesKey("selected_fame_field")
        val SHOULD_SHOW_NEXT_CARD_TYPE = booleanPreferencesKey("should_show_next_card_type")
    }

    companion object {
        private val DataStore<Preferences>.safetyData: Flow<Preferences>
            get() = data.catch { emit(emptyPreferences()) }
        private val Preferences.isDefaultScreenType: Boolean
            get() = this[IS_DEFAULT_SCREEN_TYPE] ?: false
        private val Preferences.selectedGameField: Int?
            get() = this[SELECTED_GAME_FIELD]
        private val Preferences.shouldShowNextCardType: Boolean
            get() = this[SHOULD_SHOW_NEXT_CARD_TYPE] ?: false
    }
}