package ru.bepis.mooncompanion.ui.gamecreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.bepis.mooncompanion.repository.GameSettingRepository
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GameCreationViewModel(
    private val gameSettingRepository: GameSettingRepository
) : ViewModel() {

    val uiState = gameSettingRepository.selectedGameField
        .map { UiState.SelectedGameField(it) }
        .stateIn(
            viewModelScope,
            WhileSubscribed(5_000),
            UiState.NoInfo
        )

    fun saveGameField(gameField: Int) {
        gameSettingRepository.saveGameField(gameField)
    }

    sealed interface UiState {
        @JvmInline
        value class SelectedGameField(val value: Int) : UiState
        object NoInfo : UiState
    }
}