package ru.bepis.mooncompanion.ui.game

import kotlinx.coroutines.flow.MutableStateFlow
import ru.bepis.mooncompanion.decktools.DeckGenerator.Companion.shuffleDeck
import ru.bepis.mooncompanion.domain.Card
import ru.bepis.mooncompanion.domain.CardDeck
import ru.bepis.mooncompanion.domain.CardType

data class DeckState(
    val firstColumn: CardState,
    val secondColumn: CardState,
    val thirdColumn: CardState,
    val isFirstTurn: Boolean
)

data class CardState(
    val cardNumber: Int,
    val cardType: CardType,
    val secondCardType: CardType
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

    private fun makeCard(column: List<Card>): CardState {
        val (first, second) = column.subList(cache, cache + 2)
        return CardState(
            cardNumber = second.number,
            cardType = first.type,
            secondCardType = second.type
        )
    }
}