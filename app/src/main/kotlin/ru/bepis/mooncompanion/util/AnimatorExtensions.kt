package ru.bepis.mooncompanion.util

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.Resources.NotFoundException
import androidx.annotation.AnimatorRes
import androidx.core.animation.addListener
import ru.bepis.mooncompanion.util.AnimationState.IDLE
import ru.bepis.mooncompanion.util.AnimationState.PROGRESS
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class AnimationState {
    IDLE, PROGRESS
}

fun Animator.animationFlow(): Flow<AnimationState> = callbackFlow {
    trySend(IDLE)
    val listener = addListener(
        onStart = { trySend(PROGRESS) },
        onEnd = { trySend(IDLE) }
    )
    awaitClose { removeListener(listener) }
}

@Throws(NotFoundException::class)
inline fun <reified T : Animator> Context.loadAnimator(@AnimatorRes id: Int): T =
    AnimatorInflater.loadAnimator(this, id) as T