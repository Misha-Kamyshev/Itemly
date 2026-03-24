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
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
        setupBackPressed()

        binding.swipeRefreshHome.setOnRefreshListener {
            viewModel.refresh(requireContext())
            binding.swipeRefreshHome.isRefreshing = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = AdapterImageView(
            mutableListOf(),
            { data ->
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)

                (activity as? MainActivity)?.openDetailFragment(DetailImageFragment.newInstance(data))
            },
            {}
        )

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
        binding.editSearch.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    searchJob?.cancel()

                    searchJob = lifecycleScope.launch {
                        delay(500)

                        val query = s.toString()

                        if (query.isBlank()) {
                            viewModel.clearSearch(requireContext())
                        } else {
                            viewModel.startSearch(query, requireContext())
                        }
                    }
                }
            })

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
                    binding.editSearch.clearFocus()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setupBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val text = binding.editSearch.text.toString()
            if (text.isNotEmpty()) {
                binding.editSearch.text?.clear()
                binding.editSearch.clearFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }
}
