package com.example.itemly.ui.authorization

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.itemly.data.api.ApiClient
import com.example.itemly.R
import com.example.itemly.data.model.DataSpanConfig
import com.example.itemly.data.model.authorization.DataRegistrationPush
import com.example.itemly.databinding.FragmentRegBinding
import com.example.itemly.ui.main.AuthActivity
import com.example.itemly.utils.buildColoredSpannable
import com.example.itemly.utils.nextFocus
import com.example.itemly.utils.saveToken
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
            (requireActivity() as AuthActivity).openFragment("AUTHORIZATION", AuthFragment())
        }
        binding.errorSignUpTextView.visibility = View.GONE

        binding.editUsernameSignUp.nextFocus(binding.editEmailSignUp)
        binding.editEmailSignUp.nextFocus(binding.editPasswordSignUp)
        binding.editPasswordSignUp.nextFocus(binding.editPasswordSecondSignUp)
        binding.editPasswordSecondSignUp.nextFocus { onClickButtonSingUp() }

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
        request(username, email, password)
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

    private fun request(username: String, email: String, password: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.buttonSignUp.isEnabled = false

                val response = ApiClient.apiService.signUp(
                    request = DataRegistrationPush(
                        username = username,
                        email = email,
                        password = password
                    )
                )
                saveToken(requireContext(), response)

                binding.editUsernameSignUp.setText("")
                binding.editEmailSignUp.setText("")
                binding.editPasswordSignUp.setText("")
                binding.editPasswordSecondSignUp.setText("")

                val imm =
                    requireView().context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                (requireActivity() as AuthActivity).startMain()
            } catch (_: HttpException) {
                binding.errorSignUpTextView.text = "Ошибка сервера, попробуйте позже"
                binding.errorSignUpTextView.visibility = View.VISIBLE
            } catch (_: IOException) {
                binding.errorSignUpTextView.text = "Ошибка сети, попробуйте позже"
                binding.errorSignUpTextView.visibility = View.VISIBLE
            } finally {
                binding.buttonSignUp.isEnabled = true
            }
        }
    }
}
