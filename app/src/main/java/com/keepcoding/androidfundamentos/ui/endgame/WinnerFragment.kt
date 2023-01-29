package com.keepcoding.androidfundamentos.ui.endgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.keepcoding.androidfundamentos.databinding.FragmentWinnerBinding
import com.keepcoding.androidfundamentos.model.Hero

class WinnerFragment(private val heroAlive: Hero): Fragment() {

    private var _binding: FragmentWinnerBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWinnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.winnerImage.load(heroAlive.photo)
        binding.winnerCurrentHealth.text = heroAlive.currentHealth.toString()
        binding.winnerName.text = heroAlive.name
        binding.winnerMaxHealth.text = heroAlive.maxHealth.toString()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}