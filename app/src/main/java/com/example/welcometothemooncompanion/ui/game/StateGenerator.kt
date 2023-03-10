package com.example.welcometothemooncompanion.ui.game

import com.example.welcometothemooncompanion.decktools.DeckGenerator.Companion.shuffleDeck
import com.example.welcometothemooncompanion.domain.Card
import com.example.welcometothemooncompanion.domain.CardDeck
import kotlinx.coroutines.flow.MutableStateFlow

data class DeckState(
    val firstColumn: Card,
    val secondColumn: Card,
    val thirdColumn: Card
)

class StateGenerator(private val initDeck: CardDeck) {

    private val firstColumn = initDeck.firstColumn.toMutableList()
    private val secondColumn = initDeck.secondColumn.toMutableList()
    private val thirdColumn = initDeck.thirdColumn.toMutableList()
    private var cache = 0

    val state = MutableStateFlow(generateState())

    fun next() {
        runCatching {
            generateState()
        }.onFailure {
            fulfilledColumns()
            next()
        }.onSuccess {
            state.value = it
        }
    }

    private fun generateState() =
        DeckState(
            firstColumn = makeCard(firstColumn),
            secondColumn = makeCard(secondColumn),
            thirdColumn = makeCard(thirdColumn)
        ).also { cache++ }

    private fun fulfilledColumns() {
        val deck = initDeck.shuffleDeck()
        firstColumn += deck.firstColumn
        secondColumn += deck.secondColumn
        thirdColumn += deck.thirdColumn
    }

    fun prev() {
        if (cache <= 2) return
        cache -= 2
        next()
    }

    private fun makeCard(column: List<Card>): Card {
        val (first, second) = column.subList(cache, cache + 2)
        return Card(number = first.number, type = second.type)
    }
}