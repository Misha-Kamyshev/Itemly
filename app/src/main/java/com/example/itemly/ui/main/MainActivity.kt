package com.example.itemly.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.itemly.R
import com.example.itemly.databinding.ActivityMainBinding
import com.example.itemly.ui.account.AccountFragment
import com.example.itemly.ui.authorization.AuthFragment
import com.example.itemly.ui.buy_list.BuyFragment
import com.example.itemly.ui.home.HomeFragment
import com.example.itemly.ui.inventory.InventoryFragment
import com.example.itemly.ui.main.components.BottomBarView.Item

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomBar()
        loadingApplication()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->

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
    }

    private fun setupBottomBar() {
        binding.bottomBar.onClickListener = { item ->
            when (item) {
                Item.HOME -> openFragment(HomeFragment())
                Item.BUY -> openFragment(BuyFragment())
                Item.INVENTORY -> openFragment(InventoryFragment())
                Item.ACCOUNT -> openFragment(AccountFragment())
            }
        }
    }

    private fun loadingApplication() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLogged = prefs.getBoolean("is_logged", false)

        if (isLogged) {
            visibilityBottomBar(true)
            binding.bottomBar.select(Item.HOME)
        } else {
            visibilityBottomBar(false)
            supportFragmentManager.beginTransaction()
                .replace(binding.containerFragment.id, AuthFragment())
                .commit()
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.containerFragment.id, fragment)
            .commit()
    }

    fun visibilityBottomBar(visible: Boolean) {
        if (visible) {
            binding.bottomBar.visibility = View.VISIBLE
            binding.bottomBar.select(Item.HOME)
        }
        else
            binding.bottomBar.visibility = View.GONE
    }
}
