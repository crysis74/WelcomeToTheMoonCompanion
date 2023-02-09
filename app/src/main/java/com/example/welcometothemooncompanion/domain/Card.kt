package com.example.welcometothemooncompanion.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val number: Int,
    val type: CardType,
)

@Serializable
enum class CardType {
    @SerialName("robot")
    Robot,

    @SerialName("planning")
    Planning,

    @SerialName("water")
    Water,

    @SerialName("plant")
    Plant,

    @SerialName("energy")
    Energy,

    @SerialName("astronaut")
    Astronaut
}