package ru.bepis.mooncompanion.ui.game

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.bepis.mooncompanion.R
import ru.bepis.mooncompanion.databinding.CardLayoutBinding
import ru.bepis.mooncompanion.databinding.FmtGameBinding
import ru.bepis.mooncompanion.domain.Card
import ru.bepis.mooncompanion.domain.CardType
import ru.bepis.mooncompanion.domain.CardType.Astronaut
import ru.bepis.mooncompanion.domain.CardType.Energy
import ru.bepis.mooncompanion.domain.CardType.Planning
import ru.bepis.mooncompanion.domain.CardType.Plant
import ru.bepis.mooncompanion.domain.CardType.Robot
import ru.bepis.mooncompanion.domain.CardType.Water
import ru.bepis.mooncompanion.domain.ScreenType
import ru.bepis.mooncompanion.domain.ScreenType.Default
import ru.bepis.mooncompanion.domain.ScreenType.Mirrored
import ru.bepis.mooncompanion.ui.game.GameFragmentDirections.Companion.actionGameFragmentToMissionBottomSheet
import ru.bepis.mooncompanion.ui.game.GameViewModel.UiState
import ru.bepis.mooncompanion.util.observe
import ru.bepis.mooncompanion.util.setEnabledMenuItem
import ru.bepis.mooncompanion.util.setIconMenuItem

class GameFragment : Fragment(R.layout.fmt_game) {

    private val viewModel: GameViewModel by viewModel()
    private val binding: FmtGameBinding by viewBinding(FmtGameBinding::bind)

    private var isFistAppease = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewModel.uiState.observe(viewLifecycleOwner, ::renderState)
        binding.secondaryPackCard.root.rotation = 180F
        binding.root.alpha = 0f
    }

    private fun setOnClickListeners() = with(binding) {
        missionButton.setOnClickListener {
            findNavController().navigate(actionGameFragmentToMissionBottomSheet())
        }
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.back -> {
                    viewModel.onBackClicked()
                    true
                }

                R.id.close -> {
                    findNavController().popBackStack()
                    true
                }

                R.id.screenType -> {
                    viewModel.toggleScreenType()
                    true
                }

                else -> false
            }
        }
        continueBtn.setOnClickListener {
            viewModel.onContinueClicked()
        }
    }

    private fun renderState(state: UiState) {
        when (state) {
            is UiState.Content -> {
                binding.root.animate().alpha(1f).duration = 250
                renderContent(state)
            }

            UiState.Loading -> return
        }
    }

    private fun renderContent(content: UiState.Content) {
        renderButtonAppBar(content)
        renderCards(content.state)
        renderScreenType(content.screenType)
    }

    private fun renderButtonAppBar(content: UiState.Content) = with(binding.bottomAppBar) {
        setEnabledMenuItem(R.id.back, !content.state.isFirstTurn)
        val icon = when (content.screenType) {
            Default -> R.drawable.ic_default_screen
            Mirrored -> R.drawable.ic_mirrored_screen
        }
        setIconMenuItem(R.id.screenType, icon)
    }

    private fun renderCards(state: DeckState) = with(binding) {
        with(mainPackCard) {
            renderCard(firstCardLayout, state.firstColumn)
            renderCard(secondCardLayout, state.secondColumn)
            renderCard(thirdCardLayout, state.thirdColumn)
        }
        with(secondaryPackCard) {
            renderCard(firstCardLayout, state.firstColumn)
            renderCard(secondCardLayout, state.secondColumn)
            renderCard(thirdCardLayout, state.thirdColumn)
        }
    }

    private fun renderScreenType(screenType: ScreenType) = with(binding) {
        runScreenAnimation()
        secondaryPackCard.root.isVisible = screenType == Mirrored
        divider.isVisible = screenType == Mirrored
        mainPackCard.root.updateLayoutParams<ConstraintLayout.LayoutParams> {
            when (screenType) {
                Default -> {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                }

                Mirrored -> {
                    topToTop = binding.divider.id
                    height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                }
            }
        }
    }

    private fun runScreenAnimation() {
        if (!isFistAppease) {
            isFistAppease = true
            return
        }
        TransitionManager.beginDelayedTransition(binding.continueBtn)
    }

    private fun renderCard(cardLayoutBinding: CardLayoutBinding, card: Card) =
        with(cardLayoutBinding) {
            number.text = card.number.toString()
            typeImg.setImageResource(getCard(card.type))
        }

    private fun getCard(type: CardType) = when (type) {
        Robot -> R.drawable.ic_card_robot
        Planning -> R.drawable.ic_card_planning
        Water -> R.drawable.ic_card_water
        Plant -> R.drawable.ic_card_plant
        Energy -> R.drawable.ic_card_energy
        Astronaut -> R.drawable.ic_card_astronaut
    }
}