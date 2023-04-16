package ru.bepis.mooncompanion.ui.game

import ru.bepis.mooncompanion.decktools.DeckGenerator.Companion.shuffleDeck
import ru.bepis.mooncompanion.domain.Card
import ru.bepis.mooncompanion.domain.CardDeck
import kotlinx.coroutines.flow.MutableStateFlow

data class DeckState(
    val firstColumn: Card,
    val secondColumn: Card,
    val thirdColumn: Card,
    val isFirstTurn: Boolean
)

class StateGenerator(private val initDeck: CardDeck) {

    private val firstColumn = initDeck.firstColumn.toMutableList()
    private val secondColumn = initDeck.secondColumn.toMutableList()
    private val thirdColumn = initDeck.thirdColumn.toMutableList()
    private var cache = 0
    private val isFirstTurn: Boolean
        get() = cache == 1
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

    private fun generateState(): DeckState {
        val firstColumn = makeCard(firstColumn)
        val secondColumn = makeCard(secondColumn)
        val thirdColumn = makeCard(thirdColumn)
        cache++
        return DeckState(
            firstColumn = firstColumn,
            secondColumn = secondColumn,
            thirdColumn = thirdColumn,
            isFirstTurn = isFirstTurn
        )
    }


    private fun fulfilledColumns() {
        val deck = initDeck.shuffleDeck()
        firstColumn += deck.firstColumn
        secondColumn += deck.secondColumn
        thirdColumn += deck.thirdColumn
    }

    fun prev() {
        if (isFirstTurn) return
        cache -= 2
        next()
    }

    private fun makeCard(column: List<Card>): Card {
        val (first, second) = column.subList(cache, cache + 2)
        return Card(number = first.number, type = second.type)
    }
}