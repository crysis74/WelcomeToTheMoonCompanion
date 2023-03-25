package com.example.welcometothemooncompanion.ui.gamecreation

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.RadioButton
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.welcometothemooncompanion.R
import com.example.welcometothemooncompanion.databinding.FmtGameCreationBinding
import com.example.welcometothemooncompanion.ui.gamecreation.GameCreationViewModel.UiState
import com.example.welcometothemooncompanion.util.isCheckedChangeFlow
import com.example.welcometothemooncompanion.util.observe
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameCreationFragment : Fragment(R.layout.fmt_game_creation) {

    private val binding: FmtGameCreationBinding by viewBinding()
    private val viewModel: GameCreationViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.generateButton.setOnClickListener {
            findNavController().navigate(GameCreationFragmentDirections.toGameFragment())
        }
        adventureChangeFlow.observe(viewLifecycleOwner, viewModel::saveGameField)
        viewModel.uiState.observe(viewLifecycleOwner, ::renderContent)
    }

    private fun renderContent(state: UiState) = with(binding) {
        generateButton.isEnabled = state is UiState.SelectedGameField
        adventureRadioButtons.forEach { it.isEnabled = state is UiState.SelectedGameField }
        when (state) {
            is UiState.SelectedGameField -> {
                val checkedId = findGameFieldByNumber(state.value)
                adventureRadioButtons.first { checkedId == it.id }.isChecked = true
            }
            UiState.NoInfo -> return
        }
    }

    private val adventureChangeFlow: Flow<Int>
        get() = adventureRadioButtons
            .map(::isCheckedChangeFlow)
            .merge()
            .drop(1)
            .onEach { buttonId ->
                adventureRadioButtons
                    .filter { it.id != buttonId }
                    .forEach { it.isChecked = false }
            }
            .map { findGameFieldByIdRes(it) }

    private val adventureRadioButtons: List<RadioButton>
        get() = with(binding) {
            listOf(
                adventure1Button,
                adventure2Button,
                adventure3Button,
                adventure4Button,
                adventure5Button,
                adventure6Button,
                adventure7Button,
                adventure8Button
            )
        }

    companion object {
        private val gameFieldIdMapper = listOf(
            1 to R.id.adventure1Button,
            2 to R.id.adventure2Button,
            3 to R.id.adventure3Button,
            4 to R.id.adventure4Button,
            5 to R.id.adventure5Button,
            6 to R.id.adventure6Button,
            7 to R.id.adventure7Button,
            8 to R.id.adventure8Button
        )

        private fun findGameFieldByIdRes(@IdRes idRes: Int) =
            gameFieldIdMapper.first { it.second == idRes }.first

        private fun findGameFieldByNumber(gameFieldNumber: Int) =
            gameFieldIdMapper.first { it.first == gameFieldNumber }.second
    }
}