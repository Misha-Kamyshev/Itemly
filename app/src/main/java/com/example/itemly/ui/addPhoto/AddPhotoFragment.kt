package com.example.itemly.ui.addPhoto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.itemly.R
import com.example.itemly.databinding.FragmentAddPhotoBinding

class AddPhotoFragment : Fragment() {
    private var _binding: FragmentAddPhotoBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showGalleryLayout()
            } else {
                showNoAccessLayout()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerLayoutAddPhotoFragment.visibility = View.GONE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.GONE

        checkGalleryPermission()
    }

    private fun checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showGalleryLayout()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun showGalleryLayout() {
        binding.recyclerLayoutAddPhotoFragment.visibility = View.VISIBLE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.GONE
        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )
    }

    private fun showNoAccessLayout() {
        binding.recyclerLayoutAddPhotoFragment.visibility = View.GONE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.VISIBLE
        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.bg_no_access_layout
            )
        )
    }
}
