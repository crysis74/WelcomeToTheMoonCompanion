package com.example.welcometothemooncompanion.ui.game

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.welcometothemooncompanion.R
import com.example.welcometothemooncompanion.databinding.CardLayoutBinding
import com.example.welcometothemooncompanion.databinding.FmtGameBinding
import com.example.welcometothemooncompanion.domain.Card
import com.example.welcometothemooncompanion.domain.CardType
import com.example.welcometothemooncompanion.domain.CardType.*
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
        binding.continueBtn.setOnClickListener {
            viewModel.onContinueClicked()
        }
        closeBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    private fun renderState(state: UiState) {
        when (state) {
            is UiState.Content -> renderContent(state)
            UiState.Loading -> return
        }
    }

    private fun renderContent(content: UiState.Content) = with(binding) {
        val state = content.state
        with(mainPackCard){
            renderCard(firstCardLayout, state.firstColumn)
            renderCard(secondCardLayout, state.secondColumn)
            renderCard(thirdCardLayout, state.thirdColumn)
        }
        with(secondaryPackCard){
            renderCard(firstCardLayout, state.firstColumn)
            renderCard(secondCardLayout, state.secondColumn)
            renderCard(thirdCardLayout, state.thirdColumn)
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