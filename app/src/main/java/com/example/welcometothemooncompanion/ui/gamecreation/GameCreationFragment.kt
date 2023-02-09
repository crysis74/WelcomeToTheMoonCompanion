package com.example.welcometothemooncompanion.ui.gamecreation

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.welcometothemooncompanion.R
import com.example.welcometothemooncompanion.databinding.FmtGameCreationBinding

class GameCreationFragment : Fragment(R.layout.fmt_game_creation) {

    private val binding: FmtGameCreationBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.generateButton.setOnClickListener {
            findNavController().navigate(GameCreationFragmentDirections.toGameFragment())
        }
    }
}