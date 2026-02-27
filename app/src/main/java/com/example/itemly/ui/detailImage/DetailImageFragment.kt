package com.example.itemly.ui.detailImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.model.home.HomeData
import com.example.itemly.databinding.FragmentDetailImageBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class DetailImageFragment(private val data: HomeData) : Fragment() {
    private var _binding: FragmentDetailImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainImageFragmentDetailImage.setImageResource(data.imageUrl)
        binding.buttonBackFragmentDetailImage.setOnClickListener {  }
        binding.includeBlockTagsDetailImageFragment.tagsRecyclerDetailImage.apply {
            adapter = AdapterTagsDetailImageFragment(listOf("#home", "#DIY", "#лайфхак", "#длядома", "#ремонт", "#ремонтдома")) {}
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
        binding.recyclerFragmentDetailImage.apply {
            adapter = AdapterImageView(listOf()) {}
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}
