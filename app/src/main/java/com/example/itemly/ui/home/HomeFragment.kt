package com.example.itemly.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentHomeBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.HomeViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupSearchDebounce()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = AdapterImageView(mutableListOf()) { data ->
            if (!binding.editSearch.hasFocus())
                (activity as? MainActivity)?.openDetailFragment(DetailImageFragment.newInstance(data))
        }

        binding.recyclerFragmentHome.apply {
            this.adapter = adapter
            this.layoutManager = layout
            this.addItemDecoration(
                StaggeredGridSpacingItemDecoration(2, 10, true)
            )

            setOnTouchListener { _, _ ->
                if (binding.editSearch.hasFocus()) {
                    binding.editSearch.clearFocus()

                    val sysService =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    sysService.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
                }
                false
            }
        }

        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerFragmentHome,
            adapter,
            layout,
            viewLifecycleOwner,
            viewModel
        )
    }

    private fun setupSearchDebounce() {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()

                searchJob = lifecycleScope.launch {
                    delay(500)

                    val query = s.toString()

                    if (query.isBlank()) {
                        viewModel.clearSearch(username, requireContext())
                    } else {
                        viewModel.startSearch(username, query, requireContext())
                    }
                }
            }
        })
    }
}
