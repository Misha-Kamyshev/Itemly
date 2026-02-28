package com.example.itemly.ui.authorization

import retrofit2.HttpException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.itemly.data.api.ApiClient
import com.example.itemly.R
import com.example.itemly.data.model.DataSpanConfig
import com.example.itemly.data.model.authorization.DataAuthorizationPush
import com.example.itemly.databinding.FragmentAuthBinding
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.utils.buildColoredSpannable
import com.example.itemly.utils.nextFocus
import com.example.itemly.utils.saveToken
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

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
        binding.hrefSignUp.setOnClickListener {
            (requireActivity() as MainActivity).openMainFragment("REGISTRATION", RegFragment())
        }

        binding.errorSignInTextView.visibility = View.GONE
        binding.editLoginSignIn.nextFocus(binding.editPasswordSignIn)
        binding.editPasswordSignIn.nextFocus { onClickButtonSignIn() }

        paintText()
    }

    private fun onClickButtonSignIn() {
        val login: String = binding.editLoginSignIn.text.toString()
        val password: String = binding.editPasswordSignIn.text.toString()

        if (login.isEmpty() || password.isEmpty()) {
            binding.errorSignInTextView.text = "Заполните все поля"
            binding.errorSignInTextView.visibility = View.VISIBLE
            return
        }
        binding.errorSignInTextView.visibility = View.GONE
        request(login, password)
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

    private fun request(login: String, password: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.signIn(
                    request = DataAuthorizationPush(
                        login = login,
                        password = password
                    )
                )
                saveToken(requireContext(), response)

                (requireActivity() as MainActivity).onLoginSuccess()
            } catch (e: HttpException) {
                val errorJson = e.response()?.errorBody()?.string()
                    ?: "{\"detail\": \"Ошибка сервера\"}"
                val detail = JSONObject(errorJson).getString("detail")

                binding.errorSignInTextView.text = detail
                binding.errorSignInTextView.visibility = View.VISIBLE
            } catch (e: IOException) {
                binding.errorSignInTextView.text = "Ошибка сети, попробуйте позже"
                binding.errorSignInTextView.visibility = View.VISIBLE
            }
        }
    }
}
