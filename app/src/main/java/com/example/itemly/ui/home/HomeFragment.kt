package com.example.itemly.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.databinding.FragmentHomeBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerFragmentHome.apply {
            adapter = AdapterImageView(
                listOf()
            )
            { item ->
                if (!binding.editSearch.hasFocus())
                    (activity as? MainActivity)?.openFragment(DetailImageFragment(item))
            }

            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

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
    }
}
