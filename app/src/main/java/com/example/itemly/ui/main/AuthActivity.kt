package com.example.itemly.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.ActivityAuthBinding
import com.example.itemly.ui.authorization.AuthFragment

class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(PrefKeys.PREF_USER, MODE_PRIVATE)
        val isLogged = prefs.getBoolean(PrefKeys.IS_LOGIN, false)

        if (isLogged) {
            startMain()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )

            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.authContainer.id, AuthFragment())
                .commit()
        }
    }

    fun openFragment(tag: String, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.authContainer.id, fragment, tag)
            .commit()
    }

    fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
