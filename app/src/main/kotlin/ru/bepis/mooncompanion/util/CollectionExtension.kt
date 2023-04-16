package ru.bepis.mooncompanion.util

fun <T> List<T>.update(oldValue: T, newValue: T): List<T> =
    toMutableList().apply {
        val index = indexOf(oldValue)
        this[index] = newValue
    }