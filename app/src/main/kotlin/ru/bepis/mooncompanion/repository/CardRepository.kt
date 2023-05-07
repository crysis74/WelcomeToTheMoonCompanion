package ru.bepis.mooncompanion.repository

import android.content.res.AssetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.bepis.mooncompanion.domain.Card

@OptIn(ExperimentalSerializationApi::class)
class CardRepository(
    private val assets: AssetManager,
    private val json: Json
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val cardsDeferred: Deferred<List<Card>> = scope.async {
        assets.open(DECK_JSON_FILE_NAME).use {
            json.decodeFromStream(it)
        }
    }

    suspend fun getCards() = cardsDeferred.await()

    companion object {
        private const val DECK_JSON_FILE_NAME = "deck.json"
    }
}


