package com.example.welcometothemooncompanion.di

import android.content.res.AssetManager
import com.example.welcometothemooncompanion.decktools.DeckGenerator
import com.example.welcometothemooncompanion.repository.CardRepository
import com.example.welcometothemooncompanion.ui.game.GameViewModel
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single<Json> { Json }
    single<AssetManager> { androidContext().assets }
    viewModelOf(::GameViewModel)
    singleOf(::CardRepository)
    singleOf(::DeckGenerator)
}