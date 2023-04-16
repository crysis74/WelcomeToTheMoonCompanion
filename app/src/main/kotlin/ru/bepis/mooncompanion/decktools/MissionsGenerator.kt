package ru.bepis.mooncompanion.decktools

import ru.bepis.mooncompanion.domain.Mission
import ru.bepis.mooncompanion.domain.MissionType
import ru.bepis.mooncompanion.repository.MissionRepository

class MissionsGenerator(private val missionRepository: MissionRepository) {

    suspend fun generateMissions(fieldNumber: Int): Map<MissionType, Mission> =
        missionRepository.getMissionsByField(fieldNumber)
            .groupBy { it.type }
            .mapValues { it.value.random() }
}