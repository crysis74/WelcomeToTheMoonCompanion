package ru.bepis.mooncompanion.domain

data class CardDeck(
    val firstColumn: List<Card>,
    val secondColumn: List<Card>,
    val thirdColumn: List<Card>
)