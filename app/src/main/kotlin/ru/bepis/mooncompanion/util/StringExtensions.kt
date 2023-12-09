package ru.bepis.mooncompanion.util

fun String?.takeIfNotNullOrEmpty() = takeUnless { it.isNullOrEmpty() }