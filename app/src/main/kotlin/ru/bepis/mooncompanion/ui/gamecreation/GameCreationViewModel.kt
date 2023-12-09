package ru.bepis.mooncompanion.ui.gamecreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.bepis.mooncompanion.R
import ru.bepis.mooncompanion.repository.GameSettingRepository
import ru.bepis.mooncompanion.util.NativeText
import ru.bepis.mooncompanion.util.toNativeText

class GameCreationViewModel(
    private val gameSettingRepository: GameSettingRepository
) : ViewModel() {

    sealed interface UiState {
        data class Content(val fields: List<GameField>, val selectedGameFieldId: Int) : UiState
        data object NoInfo : UiState
    }

    data class GameField(val id: Int, val name: NativeText)

    val uiState = gameSettingRepository.selectedGameField
        .map {
            UiState.Content(
                fields = allGameField,
                selectedGameFieldId = it ?: DEFAULT_GAME_FIELD
            )
        }
        .stateIn(
            viewModelScope,
            WhileSubscribed(5_000),
            UiState.NoInfo
        )

    fun saveGameField(gameFieldId: Int) {
        gameSettingRepository.saveGameField(gameFieldId)
    }

    companion object {
        private val allGameField: List<GameField> = listOf(
            GameField(1, R.string.adventure_1.toNativeText()),
            GameField(2, R.string.adventure_2.toNativeText()),
            GameField(3, R.string.adventure_3.toNativeText()),
            GameField(4, R.string.adventure_4.toNativeText()),
            GameField(5, R.string.adventure_5.toNativeText()),
            GameField(6, R.string.adventure_6.toNativeText()),
            GameField(7, R.string.adventure_7.toNativeText()),
            GameField(8, R.string.adventure_8.toNativeText()),
        )
        private val DEFAULT_GAME_FIELD = allGameField.first().id
    }
}