package com.example.welcometothemooncompanion.ui.mission

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.welcometothemooncompanion.decktools.MissionsGenerator
import com.example.welcometothemooncompanion.domain.Mission
import com.example.welcometothemooncompanion.domain.MissionPoint
import com.example.welcometothemooncompanion.domain.MissionType
import com.example.welcometothemooncompanion.domain.MissionType.*
import com.example.welcometothemooncompanion.repository.GameSettingRepository
import com.example.welcometothemooncompanion.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MissionViewModel(
    private val gameSettingRepository: GameSettingRepository,
    private val missionsGenerator: MissionsGenerator
) : ViewModel() {

    val uiState = MutableStateFlow<List<MissionItem>?>(null)

    init {
        viewModelScope.launch {
            val gameField = gameSettingRepository.selectedGameField.first()
            updateUiState(missionsGenerator.generateMissions(gameField)
                .mapValues { entry -> entry.value.toMissionItem() }
                .toMissionItems()
            )
        }
    }

    fun changeMissionCompletionState(item: MissionItem) {
        val value = uiState.value ?: return
        val updatedItem = item.copy(isCompleted = !item.isCompleted)
        updateUiState(value.update(item, updatedItem))
    }

    private fun updateUiState(state: List<MissionItem>) {
        require(state.size == 3)
        require(state.map { it.type }.containsAll(MissionType.values().toList()))
        uiState.value = state
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
    val isCompleted: Boolean = false
)

fun Mission.toMissionItem() = MissionItem(
    imageRes = imageRes,
    firstPlace = firstPlace,
    otherPlaces = otherPlaces,
    type = type,
)