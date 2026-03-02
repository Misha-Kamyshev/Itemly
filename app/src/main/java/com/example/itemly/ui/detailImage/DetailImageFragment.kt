package com.example.itemly.ui.detailImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.itemly.data.api.ApiConstants
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.databinding.FragmentDetailImageBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class DetailImageFragment(private val data: ItemDataSchema) : Fragment() {
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

        Glide.with(binding.mainImageFragmentDetailImage.context)
            .load(ApiConstants.BASE_URL + data.imageUrl)
            .into(binding.mainImageFragmentDetailImage)

        binding.buttonBackFragmentDetailImage.setOnClickListener { }
        binding.includeBlockTagsDetailImageFragment.tagsRecyclerDetailImage.apply {
            adapter = AdapterTagsDetailImageFragment(listOf("#home", "#DIY", "#лайфхак", "#длядома", "#ремонт", "#ремонтдома")) {}
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
        binding.recyclerFragmentDetailImage.apply {
            adapter = AdapterImageView(mutableListOf()) {}
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}
