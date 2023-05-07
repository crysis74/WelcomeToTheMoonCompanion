package ru.bepis.mooncompanion.repository.model

import kotlinx.serialization.Serializable
import ru.bepis.mooncompanion.domain.MissionType

@Serializable
data class MissionResp(
    val fieldNumber: Int,
    val values: List<MissionRespExtra>
) {
    @Serializable
    data class MissionRespExtra(
        val type: MissionType,
        val imageOrdinal: Int,
        val description: String,
        val firstPlace: Int? = null,
        val otherPlaces: Int? = null
    )
}