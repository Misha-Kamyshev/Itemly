package com.example.itemly.ui.addPhoto

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.itemly.R
import com.example.itemly.databinding.FragmentAddPhotoBinding
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.previewImage.PreviewImageFragment
import com.example.itemly.utils.GridSpacingItemDecoration
import com.example.itemly.utils.loadImages

class AddPhotoFragment : Fragment() {
    private var _binding: FragmentAddPhotoBinding? = null
    private val binding get() = _binding!!
    private var selectedImage: Uri? = null
    private lateinit var cameraPhotoUri: Uri

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
        binding.galleryLayoutAddPhotoFragment.visibility = View.GONE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.GONE

        checkGalleryPermission()
        binding.acceptSelectedFragmentAddPhoto.setOnClickListener {
            val fragment = PreviewImageFragment().apply {
                arguments = Bundle().apply {
                    putString("photo_uri", selectedImage.toString())
                }
            }
            (activity as? MainActivity)?.openFragment(fragment)
        }
    }

    private fun getReadPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun checkGalleryPermission() {
        val permission = getReadPermission()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showGalleryLayout()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun showGalleryLayout() {
        binding.galleryLayoutAddPhotoFragment.visibility = View.VISIBLE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.GONE

        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_image_gallery)

        binding.recyclerLayoutAddPhotoFragment.apply {
            adapter = AdapterGalleryLayout(
                loadImages(requireContext()),
                onClickCamera = {
                    cameraPhotoUri = createImageUri()
                    takePictureLauncher.launch(cameraPhotoUri)
                },
                onClickImage = { item ->
                    if (item != null) {
                        binding.acceptSelectedFragmentAddPhoto.visibility = View.VISIBLE
                        this@AddPhotoFragment.selectedImage = item
                    } else
                        binding.acceptSelectedFragmentAddPhoto.visibility = View.GONE
                })

            layoutManager = GridLayoutManager(
                requireContext(),
                3,
                GridLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(
                GridSpacingItemDecoration(
                    3,
                    spacing,
                    true
                )
            )
        }
    }

    private fun showNoAccessLayout() {
        binding.galleryLayoutAddPhotoFragment.visibility = View.GONE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.VISIBLE
        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.bg_no_access_layout
            )
        )
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val fragment = PreviewImageFragment().apply {
                arguments = Bundle().apply {
                    putString("photo_uri", cameraPhotoUri.toString())
                }
            }
            (activity as? MainActivity)?.openFragment(fragment)
        }
    }

    private fun createImageUri(): Uri {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
    }
}
