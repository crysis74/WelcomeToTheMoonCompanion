package com.example.welcometothemooncompanion.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.welcometothemooncompanion.decktools.DeckGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val deckGenerator: DeckGenerator
) : ViewModel() {

    private lateinit var stateGenerator: StateGenerator
    val uiState = MutableStateFlow<UiState>(UiState.Loading)

    init {
        viewModelScope.launch {
            stateGenerator = StateGenerator(deckGenerator.generateDeck())
            stateGenerator.state.collect {
                uiState.value = UiState.Content(it)
            }
        }
    }

    fun onBackClicked() = stateGenerator.prev()
    fun onContinueClicked() = stateGenerator.next()

    sealed interface UiState {
        @JvmInline
        value class Content(val state: DeckState) : UiState
        object Loading : UiState
    }
}