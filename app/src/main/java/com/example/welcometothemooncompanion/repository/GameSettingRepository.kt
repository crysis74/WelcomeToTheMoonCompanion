package com.example.welcometothemooncompanion.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.welcometothemooncompanion.domain.ScreenType
import com.example.welcometothemooncompanion.repository.GameSettingRepository.PreferencesKey.IS_DEFAULT_SCREEN_TYPE
import com.example.welcometothemooncompanion.repository.GameSettingRepository.PreferencesKey.SELECTED_GAME_FIELD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GameSettingRepository(
    private val dataStore: DataStore<Preferences>,
    private val appScope: CoroutineScope
) {

    val screenType: Flow<ScreenType> = dataStore.data.map {
        fun Boolean.toScreenType() = if (this) ScreenType.Default else ScreenType.Mirrored
        it.isDefaultScreenType.toScreenType()
    }

    val selectedGameField: Flow<Int> = dataStore.data.map {
        it.selectedGameField
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

    private object PreferencesKey {
        val IS_DEFAULT_SCREEN_TYPE = booleanPreferencesKey("is_default_screen_type")
        val SELECTED_GAME_FIELD = intPreferencesKey("selected_fame_field")
    }

    companion object {
        private val Preferences.isDefaultScreenType: Boolean
            get() = this[IS_DEFAULT_SCREEN_TYPE] ?: false
        private val Preferences.selectedGameField: Int
            get() = this[SELECTED_GAME_FIELD] ?: 1
    }
}