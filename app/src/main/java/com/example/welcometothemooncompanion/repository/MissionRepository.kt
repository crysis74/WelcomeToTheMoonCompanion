package com.example.welcometothemooncompanion.repository

import android.content.res.AssetManager
import com.example.welcometothemooncompanion.domain.toMission
import com.example.welcometothemooncompanion.repository.model.MissionResp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class MissionRepository(
    private val assets: AssetManager,
    private val json: Json,
    private val missionImageResGenerator: MissionImageResGenerator
) {
    suspend fun getMissionsByField(fieldNumber: Int) =
        getMissions()
            .filter { it.fieldNumber == fieldNumber }
            .flatMap { it.values }
            .map {
                val imageRes = missionImageResGenerator.generate(fieldNumber, it)
                it.toMission(fieldNumber, imageRes)
            }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun getMissions(): List<MissionResp> = withContext(Dispatchers.Default) {
        assets.open(MISSIONS_JSON_FILE_NAME).use {
            json.decodeFromStream(it)
        }
    }

    companion object {
        private const val MISSIONS_JSON_FILE_NAME = "missions.json"
    }
}