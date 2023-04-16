package ru.bepis.mooncompanion.repository.model

import ru.bepis.mooncompanion.domain.MissionType
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