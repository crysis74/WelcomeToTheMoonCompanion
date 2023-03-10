package com.example.welcometothemooncompanion.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.welcometothemooncompanion.domain.ScreenType
import com.example.welcometothemooncompanion.repository.GameSettingRepository.PreferencesKey.IS_DEFAULT_SCREEN_TYPE
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

    fun toggleScreenType() {
        appScope.launch {
            dataStore.edit {
                it[IS_DEFAULT_SCREEN_TYPE] = !it.isDefaultScreenType
            }
        }
    }

    private object PreferencesKey {
        val IS_DEFAULT_SCREEN_TYPE = booleanPreferencesKey("is_default_screen_type")
    }

    companion object {
        private val Preferences.isDefaultScreenType: Boolean
            get() = this[IS_DEFAULT_SCREEN_TYPE] ?: false
    }
}