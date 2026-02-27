package com.example.itemly.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itemly.databinding.FragmentAddBinding
import com.example.itemly.ui.main.MainActivity

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backgroundFragmentAdd.setOnClickListener { (activity as? MainActivity)?.closeAddFragment() }
        binding.closeFragmentAdd.setOnClickListener { (activity as? MainActivity)?.closeAddFragment() }
    }
}