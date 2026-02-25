package com.example.itemly.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.R
import com.example.itemly.data.model.home.HomeData
import com.example.itemly.databinding.FragmentHomeBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerFragmentHome.adapter = AdapterHomeFragment(
            listOf(
                HomeData(1, R.drawable.q),
                HomeData(2, R.drawable.w),
                HomeData(3, R.drawable.e),
                HomeData(4, R.drawable.r),
                HomeData(5, R.drawable.t),
                HomeData(6, R.drawable.y),
            )
        )
        binding.recyclerFragmentHome.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }
}
