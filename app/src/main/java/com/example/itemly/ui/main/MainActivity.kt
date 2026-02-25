package com.example.itemly.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.itemly.data.PrefKeys
import com.example.itemly.databinding.ActivityMainBinding
import com.example.itemly.ui.AddFragment
import com.example.itemly.ui.FavoriteFragment
import com.example.itemly.ui.MyImageFragment
import com.example.itemly.ui.account.AccountFragment
import com.example.itemly.ui.authorization.AuthFragment
import com.example.itemly.ui.home.HomeFragment
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
                Item.FAVORITE -> openFragment(FavoriteFragment())
                Item.ADD -> openAddFragment()
                Item.MY_IMAGE -> openFragment(MyImageFragment())
                Item.ACCOUNT -> openFragment(AccountFragment())
            }
        }
    }

    private fun loadingApplication() {
        val prefs = getSharedPreferences(PrefKeys.PREF_USER, MODE_PRIVATE)
        val isLogged = prefs.getBoolean(PrefKeys.IS_LOGIN, false)

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

    private fun openAddFragment() {
        supportFragmentManager.beginTransaction()
            .add(binding.containerFragment.id, AddFragment(), "ADD_FRAGMENT")
            .commit()
    }

    fun closeAddFragment() {
        supportFragmentManager.beginTransaction()
            .remove(supportFragmentManager.findFragmentByTag("ADD_FRAGMENT")!!)
            .commit()
        binding.bottomBar.restorePrevious()
    }

    fun visibilityBottomBar(visible: Boolean) {
        if (visible) {
            binding.bottomBar.visibility = View.VISIBLE
            binding.bottomBar.select(Item.HOME)
        } else
            binding.bottomBar.visibility = View.GONE
    }
}
