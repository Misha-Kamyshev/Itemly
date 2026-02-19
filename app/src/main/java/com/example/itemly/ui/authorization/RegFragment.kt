package com.example.itemly.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.itemly.R
import com.example.itemly.data.model.DataSpanConfig
import com.example.itemly.databinding.FragmentRegBinding
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.utils.buildColoredSpannable
import com.example.itemly.utils.nextFocus

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
        binding.hrefSignIn.setOnClickListener {
            (requireActivity() as MainActivity).openFragment(AuthFragment())
        }
        binding.errorSignUpTextView.visibility = View.GONE

        paintText()
    }

    private fun onClickButtonSingUp() {
        val username: String = binding.editUsernameSignUp.text.toString()
        val email: String = binding.editEmailSignUp.text.toString()
        val password: String = binding.editPasswordSignUp.text.toString()
        val passwordSecond: String = binding.editPasswordSecondSignUp.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordSecond.isEmpty()) {
            binding.errorSignUpTextView.text = "Заполните все поля"
            binding.errorSignUpTextView.visibility = View.VISIBLE
            return
        } else if (password != passwordSecond) {
            binding.errorSignUpTextView.text = "Пароли не совпадают"
            binding.errorSignUpTextView.visibility = View.VISIBLE
            return
        }
    }


    private fun paintText() {
        val fullText: String = ContextCompat.getString(binding.root.context, R.string.href_sign_in)
        val highlightText = "Войти"
        val start = fullText.indexOf(highlightText)

        val spannable = buildColoredSpannable(
            context = binding.root.context,
            fullText = fullText,
            spans = listOf(
                DataSpanConfig(
                    start = start,
                    end = start + highlightText.length,
                    colorRes = R.color.main_color
                )
            )
        )
        binding.hrefSignIn.text = spannable
    }
}
