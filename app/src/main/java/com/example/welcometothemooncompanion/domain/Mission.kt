package com.example.welcometothemooncompanion.domain

import androidx.annotation.DrawableRes
import com.example.welcometothemooncompanion.R
import com.example.welcometothemooncompanion.domain.MissionPoint.Image
import com.example.welcometothemooncompanion.domain.MissionPoint.Number
import com.example.welcometothemooncompanion.repository.model.MissionResp

data class Mission(
    val fieldNumber: Int,
    val type: MissionType,
    @DrawableRes
    val imageRes: Int,
    val firstPlace: MissionPoint,
    val otherPlaces: MissionPoint
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
    otherPlaces = otherPlaces?.let { Number(it) } ?: Image(R.drawable.ic_star_second_places)
)