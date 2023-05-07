package ru.bepis.mooncompanion.domain

import androidx.annotation.DrawableRes
import ru.bepis.mooncompanion.R
import ru.bepis.mooncompanion.domain.MissionPoint.Image
import ru.bepis.mooncompanion.domain.MissionPoint.Number
import ru.bepis.mooncompanion.repository.model.MissionResp

data class Mission(
    val fieldNumber: Int,
    val type: MissionType,
    @DrawableRes
    val imageRes: Int,
    val firstPlace: MissionPoint,
    val otherPlaces: MissionPoint,
    val description: String
)

enum class MissionType {
    A, B, C
}

sealed interface MissionPoint {
    @JvmInline
    value class Number(val value: Int) : MissionPoint

    @JvmInline
    value class Image(@DrawableRes val drawableRes: Int) : MissionPoint
}

fun MissionResp.MissionRespExtra.toMission(
    fieldNumber: Int,
    @DrawableRes imageRes: Int
) = Mission(
    fieldNumber = fieldNumber,
    type = type,
    imageRes = imageRes,
    firstPlace = firstPlace?.let { Number(it) } ?: Image(R.drawable.ic_star_first_place),
    otherPlaces = otherPlaces?.let { Number(it) } ?: Image(R.drawable.ic_star_second_places),
    description = description
)