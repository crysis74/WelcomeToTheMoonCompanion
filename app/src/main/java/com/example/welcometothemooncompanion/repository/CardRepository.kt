package com.example.welcometothemooncompanion.repository

import android.content.res.AssetManager
import com.example.welcometothemooncompanion.domain.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class CardRepository(
    private val assets: AssetManager,
    private val json: Json
) {
    private lateinit var cardsDeferred: Deferred<List<Card>>
    private val scope = CoroutineScope(Dispatchers.Default)

    fun init() {
        cardsDeferred = scope.async {
            assets.open(DECK_JSON_FILE_NAME).use {
                json.decodeFromStream(it)
            }
        }
    }

    suspend fun getCards() = cardsDeferred.await()

    companion object {
        private const val DECK_JSON_FILE_NAME = "deck.json"
    }
}


