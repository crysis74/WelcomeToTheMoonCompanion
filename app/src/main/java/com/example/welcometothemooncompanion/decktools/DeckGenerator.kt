package com.example.welcometothemooncompanion.decktools

import com.example.welcometothemooncompanion.domain.CardDeck
import com.example.welcometothemooncompanion.repository.CardRepository

class DeckGenerator(private val cardRepository: CardRepository) {

    suspend fun generateDeck(): CardDeck {
        val cards = cardRepository.getCards().shuffled()
        val chunkedCards = cards.chunked(COLUMN_CARD_SIZE)
        return CardDeck(
            firstColumn = chunkedCards[0],
            secondColumn = chunkedCards[1],
            thirdColumn = chunkedCards[2]
        ).shuffleDeck()
    }

    companion object {
        private const val COLUMN_CARD_SIZE = 21
        fun CardDeck.shuffleDeck() = copy(
            firstColumn = firstColumn.shuffled(),
            secondColumn = secondColumn.shuffled(),
            thirdColumn = thirdColumn.shuffled()
        )
    }
}