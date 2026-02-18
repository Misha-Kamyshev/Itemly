package com.example.itemly.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itemly.databinding.FragmentRegBinding
import com.example.itemly.ui.main.MainActivity

class RegFragment : Fragment() {
    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignUp.setOnClickListener { onClickButtonSingUp() }
        binding.hrefSignIn.setOnClickListener { onClickSignIn() }
        binding.errorSignUpTextView.visibility = View.GONE
    }

    private fun onClickButtonSingUp() {
        val login: String = binding.editLoginSignUp.text.toString()
        val password: String = binding.editPasswordSignUp.text.toString()
        val passwordSecond: String = binding.editPasswordSecondSignUp.text.toString()

        if (login.isEmpty() || password.isEmpty() || passwordSecond.isEmpty()) {
            binding.errorSignUpTextView.text = "Заполните все поля"
            binding.errorSignUpTextView.visibility = View.VISIBLE
            return
        } else if (password != passwordSecond) {
            binding.errorSignUpTextView.text = "Пароли не совпадают"
            binding.errorSignUpTextView.visibility = View.VISIBLE
            return
        }
    }

    private fun onClickSignIn() {
        (requireActivity() as MainActivity).openFragment(AuthFragment())
    }
}
