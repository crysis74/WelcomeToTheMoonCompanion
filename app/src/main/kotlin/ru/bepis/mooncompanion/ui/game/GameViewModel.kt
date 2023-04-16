package ru.bepis.mooncompanion.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.bepis.mooncompanion.decktools.DeckGenerator
import ru.bepis.mooncompanion.domain.ScreenType
import ru.bepis.mooncompanion.repository.GameSettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class GameViewModel(
    private val deckGenerator: DeckGenerator,
    private val gameSettingRepository: GameSettingRepository
) : ViewModel() {

    private lateinit var stateGenerator: StateGenerator
    val uiState = MutableStateFlow<UiState>(UiState.Loading)

    init {
        viewModelScope.launch {
            stateGenerator = StateGenerator(deckGenerator.generateDeck())
            combine(
                stateGenerator.state,
                gameSettingRepository.screenType
            ) { deckState, screenType ->
                uiState.value = UiState.Content(deckState, screenType)
            }.launchIn(this)
        }
    }


    fun toggleScreenType() {
        gameSettingRepository.toggleScreenType()
    }

    fun onBackClicked() = stateGenerator.prev()
    fun onContinueClicked() = stateGenerator.next()

    sealed interface UiState {
        data class Content(val state: DeckState, val screenType: ScreenType) : UiState
        object Loading : UiState
    }
}