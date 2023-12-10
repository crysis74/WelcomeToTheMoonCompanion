package ru.bepis.mooncompanion.ui.mission

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import org.koin.android.ext.android.inject
import org.koin.androidx.navigation.koinNavGraphViewModel
import ru.bepis.mooncompanion.R
import ru.bepis.mooncompanion.databinding.AnimatedMissionLayoutBinding
import ru.bepis.mooncompanion.databinding.BtmMissionBinding
import ru.bepis.mooncompanion.domain.MissionPoint
import ru.bepis.mooncompanion.domain.MissionType
import ru.bepis.mooncompanion.domain.MissionType.A
import ru.bepis.mooncompanion.domain.MissionType.B
import ru.bepis.mooncompanion.domain.MissionType.C
import ru.bepis.mooncompanion.util.AnimationState
import ru.bepis.mooncompanion.util.animationFlow
import ru.bepis.mooncompanion.util.hideSystemUI
import ru.bepis.mooncompanion.util.observe
import ru.bepis.mooncompanion.util.requireBottomSheetDialog
import ru.bepis.mooncompanion.util.requireRootView

class MissionBottomSheet : BottomSheetDialogFragment(R.layout.btm_mission) {

    private val binding: BtmMissionBinding by viewBinding(BtmMissionBinding::bind)
    private val viewModel: MissionViewModel by koinNavGraphViewModel(R.id.gameFragment)
    private val animationGenerator: MissionBottomSheetAnimationGenerator by inject()

    private val missionLayouts: MutableList<MissionLayout> = mutableListOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        requireNotNull(dialog.window).hideSystemUI()
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMissionLayouts()
        initMissionLayoutViews()
        initDescriptionSwitch()
        initShowNextCardSwitch()
        combineTransform(
            viewModel.uiState,
            isAllAnimationsIdleFlow
        ) { state, isAllAnimationsIdle ->
            if (isAllAnimationsIdle) emit(state)
        }.observe(viewLifecycleOwner, ::renderContent)
    }

    private fun initShowNextCardSwitch() {
        binding.showNextCardSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.showNextCardType(isChecked)
        }
    }

    private fun initDescriptionSwitch() {
        setDescriptionVisibility(binding.descriptionSwitch.isChecked)
        binding.descriptionSwitch.setOnCheckedChangeListener { _, isChecked ->
            animateBottomSheet {
                setDescriptionVisibility(isChecked)
            }
        }
    }

    private fun setDescriptionVisibility(isVisible: Boolean) {
        missionLayouts
            .map { it.value.description }
            .forEach { it.isVisible = isVisible }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        missionLayouts.clear()
    }

    private fun initMissionLayoutViews() {
        missionLayouts.forEach { missionLayout ->
            with(missionLayout.value) {
                listOf(uncompletedMissionLayout, completedMissionLayout)
                    .map { it.root }
                    .forEach { it.setGoodForAnimationCameraDistance() }
                completedMissionLayout.firstPlacePoint.isInvisible = true
                missionType.setText(missionLayout.type.toStringRes())
            }
        }
    }

    private fun View.setGoodForAnimationCameraDistance() {
        val scale = resources.displayMetrics.density
        val cameraDist = CAMERA_DIST_VALUE * scale
        cameraDistance = cameraDist
    }

    private val isAllAnimationsIdleFlow: Flow<Boolean>
        get() = missionLayouts
            .map { it.frontAnimation }
            .map { it.animationFlow() }
            .let { animationsFlow ->
                combine(animationsFlow) { animationStates ->
                    animationStates.all { it == AnimationState.IDLE }
                }
            }

    private fun renderContent(uiState: MissionViewModel.UiState) = with(binding) {
        when (uiState) {
            is MissionViewModel.UiState.Content -> {
                val sortedItems = uiState.missions.sortedBy { it.type }
                val sortedMissionLayouts = missionLayouts.sortedBy { it.type }
                sortedMissionLayouts.zip(sortedItems) { layout, item ->
                    require(layout.type == item.type)
                    layout.value.renderMissionItem(item)
                    layout.value.setCompletedListener(
                        item = item,
                        frontAnimation = layout.frontAnimation,
                        backAnimation = layout.backAnimation
                    )
                }
                renderShowNextCard(uiState)
            }

            MissionViewModel.UiState.Empty -> return@with
        }

    }

    private fun renderShowNextCard(uiState: MissionViewModel.UiState.Content) {
        binding.showNextCardSwitch.isChecked = uiState.shouldShowNextCardType
    }

    private fun AnimatedMissionLayoutBinding.renderMissionItem(missionItem: MissionItem) {
        val initialGoneLayout = if (missionItem.isCompleted) {
            uncompletedMissionLayout
        } else {
            completedMissionLayout
        }
        initialGoneLayout.root.isGone = true
        listOf(completedMissionLayout, uncompletedMissionLayout).forEach {
            with(it) {
                img.setImageResource(missionItem.imageRes)
                firstPlacePoint.setMissionPoint(missionItem.firstPlace)
                otherPlacesPoint.setMissionPoint(missionItem.otherPlaces)
                description.text = missionItem.description
            }
        }
    }

    private fun TextView.setMissionPoint(point: MissionPoint) {
        when (point) {
            is MissionPoint.Number -> text = point.value.toString()
            is MissionPoint.Image -> setBackgroundResource(point.drawableRes)
        }
    }

    private fun AnimatedMissionLayoutBinding.setCompletedListener(
        item: MissionItem,
        frontAnimation: AnimatorSet,
        backAnimation: AnimatorSet
    ) {
        root.setOnClickListener {
            // Disable clicking during current animation
            root.setOnClickListener(null)
            val (visibleMission, invisibleMission) = if (item.isCompleted)
                uncompletedMissionLayout.root to completedMissionLayout.root
            else
                completedMissionLayout.root to uncompletedMissionLayout.root
            visibleMission.isVisible = true
            frontAnimation.apply {
                setTarget(invisibleMission)
                start()
            }
            backAnimation.apply {
                setTarget(visibleMission)
                start()
            }
            backAnimation.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    invisibleMission.isGone = true
                    backAnimation.removeListener(this)
                }
            })
            viewModel.changeMissionCompletionState(item)
        }
    }

    private fun initMissionLayouts() {
        val list = with(binding) {
            buildList {
                var animationPair = animationGenerator.generate()
                add(
                    MissionLayout(
                        value = AMissionLayout,
                        type = A,
                        frontAnimation = animationPair.front,
                        backAnimation = animationPair.back
                    )
                )
                animationPair = animationGenerator.generate()
                add(
                    MissionLayout(
                        value = BMissionLayout,
                        type = B,
                        frontAnimation = animationPair.front,
                        backAnimation = animationPair.back
                    )
                )
                animationPair = animationGenerator.generate()
                add(
                    MissionLayout(
                        value = CMissionLayout,
                        type = C,
                        frontAnimation = animationPair.front,
                        backAnimation = animationPair.back
                    )
                )
            }
        }
        missionLayouts.addAll(list)
    }

    private fun animateBottomSheet(action: () -> Unit) {
        val designBottomSheet = requireBottomSheetDialog().requireRootView()
        TransitionManager.beginDelayedTransition(designBottomSheet)
        action()
        TransitionManager.endTransitions(binding.root)
    }

    private data class MissionLayout(
        val value: AnimatedMissionLayoutBinding,
        val type: MissionType,
        val frontAnimation: AnimatorSet,
        val backAnimation: AnimatorSet
    )

    companion object {
        private const val CAMERA_DIST_VALUE = 8000

        @StringRes
        private fun MissionType.toStringRes() = when (this) {
            A -> R.string.mission_a
            B -> R.string.mission_b
            C -> R.string.mission_c
        }
    }
}