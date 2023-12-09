package ru.bepis.mooncompanion.ui.gamecreation

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.bepis.mooncompanion.R
import ru.bepis.mooncompanion.databinding.FmtGameCreationBinding
import ru.bepis.mooncompanion.databinding.RbtGameFieldBinding
import ru.bepis.mooncompanion.ui.gamecreation.GameCreationViewModel.GameField
import ru.bepis.mooncompanion.ui.gamecreation.GameCreationViewModel.UiState
import ru.bepis.mooncompanion.util.asString
import ru.bepis.mooncompanion.util.observe

class GameCreationFragment : Fragment(R.layout.fmt_game_creation) {

    private data class GameFieldViewWrapper(val field: GameField, val view: RadioButton)

    private val binding: FmtGameCreationBinding by viewBinding(FmtGameCreationBinding::bind)
    private val viewModel: GameCreationViewModel by viewModel()
    private var isContentAppearedAtFirstFragmentViewCreation: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.generateButton.setOnClickListener {
            findNavController().navigate(GameCreationFragmentDirections.toGameFragment())
        }
        viewModel.uiState.observe(viewLifecycleOwner, ::renderContent)
    }

    private fun renderContent(state: UiState) = with(binding) {
        generateButton.isEnabled = state is UiState.Content
        when (state) {
            is UiState.Content -> {
                if (isContentAppearedAtFirstFragmentViewCreation) {
                    // no need to recreate view hierarchy and reselect button,
                    // because of radio buttons is statefull components and field don't changed.
                    return@with
                }
                val gameFieldWrappers = generateGameFieldWrappers(state)
                addGameFieldViewsOnScreen(gameFieldWrappers)
                subscribeOnGameFieldChanging(gameFieldWrappers)
                isContentAppearedAtFirstFragmentViewCreation = true
            }

            UiState.NoInfo -> return@with
        }
    }

    private fun generateGameFieldWrappers(state: UiState.Content): List<GameFieldViewWrapper> =
        state.fields.map { gameField ->
            val gameFieldView = generateGameFieldView(gameField, state.selectedGameFieldId)
            GameFieldViewWrapper(gameField, gameFieldView)
        }

    private fun generateGameFieldView(gameField: GameField, selectedFieldId: Int): RadioButton =
        RbtGameFieldBinding.inflate(layoutInflater).root.apply {
            text = gameField.name.asString(this@GameCreationFragment)
            isChecked = gameField.id == selectedFieldId
        }

    private fun addGameFieldViewsOnScreen(buttonFields: List<GameFieldViewWrapper>) {
        buttonFields.map(GameFieldViewWrapper::view).forEach(binding.fields::addView)
    }

    private fun subscribeOnGameFieldChanging(wrappers: List<GameFieldViewWrapper>) {
        wrappers
            .map {
                it.isGameFieldSelectedFlow()
            }
            .merge()
            .observe(viewLifecycleOwner) { selectedField ->
                wrappers.filter { it.field.id != selectedField.id }
                    .forEach { it.view.isChecked = false }
                viewModel.saveGameField(selectedField.id)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isContentAppearedAtFirstFragmentViewCreation = false
    }

    companion object {
        private fun GameFieldViewWrapper.isGameFieldSelectedFlow(): Flow<GameField> =
            callbackFlow {
                view.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) trySend(field)
                }
                awaitClose { view.setOnCheckedChangeListener(null) }
            }
    }
}