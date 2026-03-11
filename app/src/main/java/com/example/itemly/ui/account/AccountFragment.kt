package com.example.itemly.ui.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentAccountBinding
import com.example.itemly.ui.add.AddFragment
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.AccountViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.logout
import com.example.itemly.utils.subscribeDataForAdapter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.core.content.edit

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by activityViewModels()
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var headerAdapter: HeaderAdapter
    private lateinit var adapterItem: AdapterImageView

    companion object {
        private const val PREF_THEME = "pref_theme"
        private const val PREF_SETTING = "setting"
        private const val PREF_SETTING_VISIBLE = "pref_setting_visible"
    }

    enum class ThemeMode {
        SYSTEM,
        DARK,
        LIGHT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = requireActivity().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        username = pref.getString(PrefKeys.USERNAME, "")!!
        email = pref.getString(PrefKeys.E_MAIL, "")!!

        setupAdapter()
        applySavedTheme()
        restoreSettingState()

        binding.swipeRefreshAccount.setOnRefreshListener {
            refreshPage()
            binding.swipeRefreshAccount.isRefreshing = false
        }

        binding.layoutSetting.apply {
            root.setOnClickListener { saveSettingState(false) }
            icClose.setOnClickListener { saveSettingState(false) }
            blockSetting.setOnClickListener {}

            changeTheme.setOnClickListener { changeTheme() }
            logoutAccount.setOnClickListener {
                logout(requireContext())
                (activity as? MainActivity)?.logoutAccount()
                restoreSettingState()
            }
        }
    }

    private suspend fun getIconAccount(): String? {
        return try {
            val response = ApiClient.apiService.getImageUser(username)
            response.pathPreview
        } catch (_: HttpException) {
            httpToast(requireContext())
            null
        } catch (_: IOException) {
            ioToast(requireContext())
            null
        }
    }

    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        adapterItem = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(DetailImageFragment.newInstance(item))
        }

        headerAdapter = HeaderAdapter(
            username = username,
            email = email,
            iconAccountUrl = null,
            userAuthor = false,
            onClickPreviewPhoto = { onClickPreviewAccount() },
            onClickSetting = { saveSettingState(true) }
        )

        val concatAdapter = ConcatAdapter(headerAdapter, adapterItem)

        binding.recyclerAccount.apply {
            this.adapter = concatAdapter
            this.layoutManager = layout
            this.addItemDecoration(StaggeredGridSpacingItemDecoration(2, 10, true))
        }

        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerAccount,
            adapterItem,
            layout,
            viewLifecycleOwner,
            viewModel
        )

        loadAvatar()
    }

    private fun onClickPreviewAccount() {
        (activity as? MainActivity)?.openDetailFragment(AddFragment.newInstance(true))
    }

    private fun loadAvatar() {
        viewLifecycleOwner.lifecycleScope.launch {
            val icon = getIconAccount()

            headerAdapter.updateIcon(icon)
            headerAdapter.notifyItemChanged(0)
        }
    }

    private fun refreshPage() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadAvatar()
            viewModel.refresh(username, requireContext())
        }
    }


    // TODO Сделать чтобы при запуске приложения сбрасывалось состояние настроек
    private fun applySavedTheme() {
        val pref = requireContext().getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE)

        val theme = ThemeMode.valueOf(
            pref.getString(PREF_THEME, ThemeMode.SYSTEM.name)!!
        )

        when (theme) {
            ThemeMode.SYSTEM -> {
                binding.layoutSetting.changeTheme.text = "Системная"
            }
            ThemeMode.DARK -> {
                binding.layoutSetting.changeTheme.text = "Тёмная"
            }
            ThemeMode.LIGHT -> {
                binding.layoutSetting.changeTheme.text = "Светлая"
            }
        }
    }

    private fun saveSettingState(isVisible: Boolean) {
        requireContext()
            .getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE)
            .edit {
                putBoolean(PREF_SETTING_VISIBLE, isVisible)
            }
        binding.layoutSetting.root.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    private fun restoreSettingState() {
        val pref = requireContext()
            .getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE)

        val visible = pref.getBoolean(PREF_SETTING_VISIBLE, false)

        binding.layoutSetting.root.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    fun changeTheme() {
        val pref = requireContext().getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE)
        val current = ThemeMode.valueOf(
            pref.getString(PREF_THEME, ThemeMode.SYSTEM.name)!!
        )

        val next = when (current) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
        }

        pref.edit { putString(PREF_THEME, next.name) }

        when (next) {
            ThemeMode.SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
                binding.layoutSetting.changeTheme.text = "Системная"
            }

            ThemeMode.DARK -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                binding.layoutSetting.changeTheme.text = "Тёмная"
            }

            ThemeMode.LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                binding.layoutSetting.changeTheme.text = "Светлая"
            }
        }
    }
}
