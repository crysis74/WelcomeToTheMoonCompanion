@file:Suppress("unused")

package ru.bepis.mooncompanion.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ru.bepis.mooncompanion.util.NativeText.Resource
import ru.bepis.mooncompanion.util.NativeText.ResourceWithSimple
import ru.bepis.mooncompanion.util.NativeText.Simple

sealed interface NativeText {
    @JvmInline
    value class Simple(val text: String) : NativeText

    @JvmInline
    value class Resource(@StringRes val id: Int) : NativeText

    data class ResourceWithSimple(@StringRes val id: Int, val args: List<Any>) : NativeText
}

fun @receiver:StringRes Int.toNativeText() = Resource(this)

fun String?.toNativeText(@StringRes id: Int) =
    this.takeIfNotNullOrEmpty()?.let { Simple(it) } ?: Resource(id)

fun String.toNativeText() = Simple(this)

fun Pair<Int, Any>.toNativeText() = ResourceWithSimple(first, listOf(second))

fun NativeText.asString(fragment: Fragment): String = asString(fragment.requireContext())

fun NativeText.asString(context: Context): String =
    when (this) {
        is Simple -> text
        is Resource -> context.getString(id)
        is ResourceWithSimple -> context.getString(id, *args.toTypedArray())
    }