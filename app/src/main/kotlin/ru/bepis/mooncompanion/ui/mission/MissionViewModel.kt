package ru.bepis.mooncompanion.ui.mission

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.bepis.mooncompanion.decktools.MissionsGenerator
import ru.bepis.mooncompanion.domain.Mission
import ru.bepis.mooncompanion.domain.MissionPoint
import ru.bepis.mooncompanion.domain.MissionType
import ru.bepis.mooncompanion.domain.MissionType.A
import ru.bepis.mooncompanion.domain.MissionType.B
import ru.bepis.mooncompanion.domain.MissionType.C
import ru.bepis.mooncompanion.repository.GameSettingRepository
import ru.bepis.mooncompanion.util.update

class MissionViewModel(
    private val gameSettingRepository: GameSettingRepository,
    private val missionsGenerator: MissionsGenerator
) : ViewModel() {

    sealed interface UiState {
        data class Content(
            val missions: List<MissionItem>,
            val shouldShowNextCardType: Boolean
        ) : UiState

        data object Empty : UiState
    }

    private val missionsFlow = MutableStateFlow<List<MissionItem>?>(null)

    val uiState: Flow<UiState> = combine(
        missionsFlow.filterNotNull(),
        gameSettingRepository.shouldShowNextCardType
    ) { missions, shouldShowNextCard ->
        UiState.Content(missions, shouldShowNextCard)
    }.stateIn(viewModelScope, WhileSubscribed(5_000), UiState.Empty)

    init {
        viewModelScope.launch {
            val gameField = gameSettingRepository.selectedGameField.first() ?: return@launch
            updateUiState(missionsGenerator.generateMissions(gameField)
                .mapValues { entry -> entry.value.toMissionItem() }
                .toMissionItems()
            )
        }
    }

    fun showNextCardType(shouldShowNextCardType: Boolean) {
        gameSettingRepository.saveShouldShowNextCardType(shouldShowNextCardType)
    }

    fun changeMissionCompletionState(item: MissionItem) {
        val value = requireNotNull(missionsFlow.value)
        val updatedItem = item.copy(isCompleted = !item.isCompleted)
        updateUiState(value.update(item, updatedItem))
    }

    private fun updateUiState(state: List<MissionItem>) {
        require(state.size == 3)
        require(state.map { it.type }.containsAll(MissionType.entries))
        missionsFlow.value = state
    }

    companion object {
        private fun Map<MissionType, MissionItem>.toMissionItems() = listOf(
            getValue(A), getValue(B), getValue(C)
        )
    }
}

data class MissionItem(
    @DrawableRes
    val imageRes: Int,
    val firstPlace: MissionPoint,
    val otherPlaces: MissionPoint,
    val type: MissionType,
    val description: String,
    val isCompleted: Boolean = false
)

fun Mission.toMissionItem() = MissionItem(
    imageRes = imageRes,
    firstPlace = firstPlace,
    otherPlaces = otherPlaces,
    type = type,
    description = description
)