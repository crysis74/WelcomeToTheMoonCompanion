package com.example.welcometothemooncompanion.ui.game

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.example.welcometothemooncompanion.R
import com.example.welcometothemooncompanion.databinding.CardLayoutBinding
import com.example.welcometothemooncompanion.databinding.FmtGameBinding
import com.example.welcometothemooncompanion.domain.Card
import com.example.welcometothemooncompanion.domain.CardType
import com.example.welcometothemooncompanion.domain.CardType.*
import com.example.welcometothemooncompanion.domain.ScreenType
import com.example.welcometothemooncompanion.domain.ScreenType.Default
import com.example.welcometothemooncompanion.domain.ScreenType.Mirrored
import com.example.welcometothemooncompanion.ui.game.GameViewModel.UiState
import com.example.welcometothemooncompanion.util.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : Fragment(R.layout.fmt_game) {

    private val viewModel: GameViewModel by viewModel()
    private val binding: FmtGameBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewModel.uiState.observe(viewLifecycleOwner, ::renderState)
        binding.secondaryPackCard.root.rotation = 180F
    }

    private fun setOnClickListeners() = with(binding) {
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
                renderContent(state)
                binding.root.isVisible = true
            }
            UiState.Loading -> binding.root.isVisible = false
        }
    }

    private fun renderContent(content: UiState.Content) = with(binding) {
        renderCards(content.state)
        renderScreenType(content.screenType)
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
        TransitionManager.beginDelayedTransition(continueBtn)
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

    private fun renderCard(cardLayoutBinding: CardLayoutBinding, card: Card) =
        with(cardLayoutBinding) {
            number.text = card.number.toString()
            typeImg.setImageResource(getCard(card.type))
        }

    private fun getCard(type: CardType) = when (type) {
        Robot -> R.drawable.robot
        Planning -> R.drawable.planning
        Water -> R.drawable.water
        Plant -> R.drawable.plant
        Energy -> R.drawable.energy
        Astronaut -> R.drawable.astronaut
    }
}