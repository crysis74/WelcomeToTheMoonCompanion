package com.example.welcometothemooncompanion.ui.mission

import android.animation.AnimatorSet
import android.content.Context
import com.example.welcometothemooncompanion.R
import com.example.welcometothemooncompanion.util.loadAnimator

class MissionBottomSheetAnimationGenerator(context: Context) {
    private val frontAnimation: AnimatorSet = context.loadAnimator(R.animator.front_animator)
    private val backAnimation: AnimatorSet = context.loadAnimator(R.animator.back_animator)
    private var isAnimInitialize: Boolean = false

    fun generate(): AnimationPair = if (!isAnimInitialize) {
        isAnimInitialize = true
        AnimationPair(frontAnimation, backAnimation)
    } else {
        AnimationPair(frontAnimation.clone(), backAnimation.clone())
    }

    data class AnimationPair(
        val front: AnimatorSet,
        val back: AnimatorSet
    )
}