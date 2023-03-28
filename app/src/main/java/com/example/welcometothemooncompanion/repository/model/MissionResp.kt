package com.example.welcometothemooncompanion.repository.model

import com.example.welcometothemooncompanion.domain.MissionType
import kotlinx.serialization.Serializable

@Serializable
data class MissionResp(
    val fieldNumber: Int,
    val values: List<MissionRespExtra>
) {
    @Serializable
    data class MissionRespExtra(
        val type: MissionType,
        val imageOrdinal: Int,
        val firstPlace: Int? = null,
        val otherPlaces: Int? = null
    )
}