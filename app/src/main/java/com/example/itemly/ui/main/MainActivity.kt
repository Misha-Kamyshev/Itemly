package com.example.itemly.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.ActivityMainBinding
import com.example.itemly.ui.add.AddFragment
import com.example.itemly.ui.favorite.FavoriteFragment
import com.example.itemly.ui.myImage.MyImageFragment
import com.example.itemly.ui.account.AccountFragment
import com.example.itemly.ui.authorization.AuthFragment
import com.example.itemly.ui.home.HomeFragment
import com.example.itemly.ui.main.components.BottomBarView.Item
import com.example.itemly.ui.viewModel.NavigationViewModel
import com.example.itemly.utils.logout

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val navigateViewModel: NavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomBar()

        if (savedInstanceState == null) {
            loadingApplication()
        } else {
            restoreStacks()
        }

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
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!handleBack()) finish()
                }
            }
        )
    }

    private fun loadingApplication() {
        val prefs = getSharedPreferences(PrefKeys.PREF_USER, MODE_PRIVATE)
        val isLogged = prefs.getBoolean(PrefKeys.IS_LOGIN, false)

        if (isLogged) {
            visibilityBottomBar(true)
            openMainFragment("HOME", HomeFragment())
        } else {
            visibilityBottomBar(false)
            openMainFragment("AUTHORIZATION", AuthFragment())
        }
    }

    private fun restoreStacks() {
        val fragments = supportFragmentManager.fragments

        navigateViewModel.mainStack.clear()
        navigateViewModel.detailStack.clear()

        fragments.forEach { fragment ->
            val tag = fragment.tag ?: return@forEach
            when {
                tag.startsWith("DETAIL_") || tag == "ADD_FRAGMENT" -> {
                    navigateViewModel.detailStack.add(fragment)
                }

                tag in listOf("HOME", "FAVORITE", "ACCOUNT", "MY_IMAGE") -> {
                    navigateViewModel.mainStack.add(tag)
                }
            }
        }
        val transaction = supportFragmentManager.beginTransaction()

        fragments.forEach {
            if (!it.isHidden) transaction.hide(it)
        }

        val topFragment =
            navigateViewModel.detailStack.lastOrNull()
                ?: navigateViewModel.mainStack.lastOrNull()?.let {
                    supportFragmentManager.findFragmentByTag(it)
                }

        topFragment?.let {
            transaction.show(it)
        }

        transaction.commitNow()

        binding.bottomBar.visibility =
            if (navigateViewModel.detailStack.isEmpty()) View.VISIBLE else View.GONE
    }

    // Настройка BottomBar
    private fun setupBottomBar() {
        binding.bottomBar.onClickListener = { item ->
            when (item) {
                Item.HOME -> openMainFragment("HOME", HomeFragment())
                Item.FAVORITE -> openMainFragment("FAVORITE", FavoriteFragment())
                Item.ADD -> openAddFragment()
                Item.MY_IMAGE -> openMainFragment("MY_IMAGE", MyImageFragment())
                Item.ACCOUNT -> openMainFragment("ACCOUNT", AccountFragment())
            }
        }
    }

    private fun visibilityBottomBar(visible: Boolean) {
        if (visible) {
            binding.bottomBar.visibility = View.VISIBLE
            binding.bottomBar.select(Item.HOME)
        } else
            binding.bottomBar.visibility = View.GONE
    }

    private fun handleBack(): Boolean {
        if (navigateViewModel.detailStack.isNotEmpty()) {
            val fragment =
                navigateViewModel.detailStack.removeAt(navigateViewModel.detailStack.lastIndex)
            supportFragmentManager.beginTransaction().remove(fragment).commitNow()

            val prev = navigateViewModel.detailStack.lastOrNull()
                ?: supportFragmentManager.findFragmentByTag(
                    navigateViewModel.mainStack.last()
                )
            prev?.let { supportFragmentManager.beginTransaction().show(it).commitNow() }

            binding.bottomBar.visibility =
                if (navigateViewModel.detailStack.isEmpty()) View.VISIBLE else View.GONE

            if (navigateViewModel.detailStack.isEmpty()) syncBottomBarSelection(navigateViewModel.mainStack.lastOrNull())
            return true
        }

        if (navigateViewModel.mainStack.size > 1) {
            val current =
                navigateViewModel.mainStack.removeAt(navigateViewModel.mainStack.lastIndex)
            val prevTag = navigateViewModel.mainStack.last()
            val transaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.findFragmentByTag(current)?.let { transaction.hide(it) }
            supportFragmentManager.findFragmentByTag(prevTag)?.let { transaction.show(it) }
            transaction.commitNow()

            syncBottomBarSelection(prevTag)
            return true
        }

        return false
    }

    private fun clearBackStack(clearMain: Boolean) {
        if (navigateViewModel.detailStack.isEmpty()) return

        val transaction = supportFragmentManager.beginTransaction()

        navigateViewModel.detailStack.forEach { fragment ->
            transaction.remove(fragment)
        }

        if (clearMain) {
            navigateViewModel.mainStack.forEach { tag ->
                supportFragmentManager.findFragmentByTag(tag)?.let {
                    transaction.remove(it)
                }
            }
        }

        transaction.commitNow()

        navigateViewModel.detailStack.clear()
        if (clearMain) {
            navigateViewModel.mainStack.clear()
        }

        binding.bottomBar.visibility = View.VISIBLE
    }

    private fun syncBottomBarSelection(tag: String? = navigateViewModel.mainStack.lastOrNull()) {
        when (tag) {
            "HOME" -> binding.bottomBar.select(Item.HOME)
            "FAVORITE" -> binding.bottomBar.select(Item.FAVORITE)
            "MY_IMAGE" -> binding.bottomBar.select(Item.MY_IMAGE)
            "ACCOUNT" -> binding.bottomBar.select(Item.ACCOUNT)
        }
    }

    // Управление фрагментами
    fun openMainFragment(tag: String, fragment: Fragment) {
        clearBackStack(false)

        val transaction = supportFragmentManager.beginTransaction()

        navigateViewModel.mainStack.lastOrNull()?.let { currentTag ->
            supportFragmentManager.findFragmentByTag(currentTag)?.let { transaction.hide(it) }
        }

        val existing = supportFragmentManager.findFragmentByTag(tag)
        if (existing != null) {
            transaction.show(existing)
        } else {
            transaction.add(binding.containerFragment.id, fragment, tag)
        }

        transaction.commitNow()

        navigateViewModel.mainStack.remove(tag)
        navigateViewModel.mainStack.add(tag)

        syncBottomBarSelection(tag)
    }

    fun openDetailFragment(detailFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.fragments
            .firstOrNull { it.isVisible }?.let { transaction.hide(it) }

        transaction.add(
            binding.containerFragment.id,
            detailFragment,
            "DETAIL_${navigateViewModel.detailStack.size}"
        )
            .commitNow()

        navigateViewModel.detailStack.add(detailFragment)
    }

    private fun openAddFragment() {
        val addFragment = AddFragment()
        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.fragments.firstOrNull { it.isVisible }?.let { transaction.hide(it) }

        transaction.add(binding.containerFragment.id, addFragment, "ADD_FRAGMENT").commit()
        navigateViewModel.detailStack.add(addFragment)

        binding.bottomBar.visibility = View.GONE
    }

    fun onLoginSuccess() {
        clearBackStack(true)

        openMainFragment("HOME", HomeFragment())

        visibilityBottomBar(true)
    }

    fun logoutAccount() {
        logout(this)
        clearBackStack(true)
        openMainFragment("AUTHORIZATION", AuthFragment())
        visibilityBottomBar(false)
    }
}
