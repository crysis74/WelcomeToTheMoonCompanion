package com.example.welcometothemooncompanion.decktools

import com.example.welcometothemooncompanion.domain.Mission
import com.example.welcometothemooncompanion.domain.MissionType
import com.example.welcometothemooncompanion.repository.MissionRepository

class MissionsGenerator(private val missionRepository: MissionRepository) {

    suspend fun generateMissions(fieldNumber: Int): Map<MissionType, Mission> =
        missionRepository.getMissionsByField(fieldNumber)
            .groupBy { it.type }
            .mapValues { it.value.random() }
}