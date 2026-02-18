package com.example.itemly.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.itemly.R
import com.example.itemly.data.model.DataSpanConfig
import com.example.itemly.databinding.FragmentAuthBinding
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.utils.buildColoredSpannable

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignIn.setOnClickListener { onClickButtonSignIn() }
        binding.hrefSignUp.setOnClickListener { onClickSingUp() }
        binding.errorSignInTextView.visibility = View.GONE

        paintText()
    }

    private fun onClickButtonSignIn() {
        val login: String = binding.editLoginSignIn.text.toString()
        val password: String = binding.editPasswordSignIn.text.toString()

        if (login.isEmpty() || password.isEmpty()) {
            binding.errorSignInTextView.text = "Заполните все поля"
            binding.errorSignInTextView.visibility = View.VISIBLE
        }
    }

    private fun onClickSingUp() {
        (requireActivity() as MainActivity).openFragment(RegFragment())
    }

    private fun paintText() {
        val fullText: String = ContextCompat.getString(binding.root.context, R.string.href_sign_up)
        val highlightText = "Зарегистрируйтесь"
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
        binding.hrefSignUp.text = spannable
    }
}
