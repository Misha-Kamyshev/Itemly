package com.example.itemly.ui.add

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.itemly.R
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.objects.CodeToken
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentAddPhotoBinding
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.previewImage.PreviewImageFragment
import com.example.itemly.utils.GridSpacingItemDecoration
import com.example.itemly.utils.loadAlbums
import com.example.itemly.utils.loadImagesFromAlbum
import com.example.itemly.utils.updateToken
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException

class AddFragment : Fragment() {
    private var _binding: FragmentAddPhotoBinding? = null
    private val binding get() = _binding!!
    private var selectedImage: Uri? = null
    private lateinit var cameraPhotoUri: Uri
    private var previewImage: Boolean = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showGalleryLayout()
            } else {
                showNoAccessLayout()
            }
        }

    companion object {
        private const val ARG_ITEM = "arg_item"

        fun newInstance(previewImage: Boolean): AddFragment {
            return AddFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_ITEM, previewImage)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        previewImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getBoolean(ARG_ITEM, false)
        } else {
            @Suppress("DEPRECATION")
            (requireArguments().getBoolean(ARG_ITEM))
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
            if (previewImage) {
                changePreviewPhoto()
            } else {
                val fragment = PreviewImageFragment().apply {
                    arguments = Bundle().apply {
                        putString("photo_uri", selectedImage.toString())
                    }
                }
                (activity as? MainActivity)?.openDetailFragment(fragment)
            }
        }

        binding.buttonBackFragmentAdd.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
        val adapter = AdapterGalleryLayout(
            onClickCamera = {
                cameraPhotoUri = createImageUri()
                takePictureLauncher.launch(cameraPhotoUri)
            },
            onClickImage = { item ->
                if (item != null) {
                    binding.acceptSelectedFragmentAddPhoto.visibility = View.VISIBLE
                    this@AddFragment.selectedImage = item
                } else
                    binding.acceptSelectedFragmentAddPhoto.visibility = View.GONE
            })


        binding.galleryLayoutAddPhotoFragment.visibility = View.VISIBLE
        binding.noAccessLayoutAddPhotoFragment.visibility = View.GONE

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_image_gallery)

        binding.recyclerLayoutAddPhotoFragment.apply {
            this.adapter = adapter
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

        listAlbum(adapter)
    }

    private fun listAlbum(adapter: AdapterGalleryLayout) {
        val albums = loadAlbums(requireContext())

        val adapterList = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            albums.map { it.name }
        )

        binding.actvAlbums.setAdapter(adapterList)

        binding.actvAlbums.doOnTextChanged { text, _, _, _ ->
            val albumName = text?.toString() ?: return@doOnTextChanged
            val selectedAlbum = albums.find { it.name == albumName } ?: return@doOnTextChanged

            val images = loadImagesFromAlbum(requireContext(), selectedAlbum.id)
            adapter.submitList(images)
        }

        binding.actvAlbums.setText(albums.first().name, false)
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
            (activity as? MainActivity)?.openDetailFragment(fragment)
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

    private fun changePreviewPhoto() {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.changePreview(
                    getImage(),
                    "Bearer $accessToken"
                )
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Успешно",
                        Toast.LENGTH_LONG
                    ).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    if (response.code() == CodeToken.ERROR_TOKEN)
                        updateToken(requireContext())
                    else
                        httpToast(requireContext())
                }
            } catch (_: IOException) {
                ioToast(requireContext())
            }
        }
    }

    private fun getImage(): MultipartBody.Part {
        val uri = selectedImage!!

        val context = requireContext()
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val inputStream = contentResolver.openInputStream(uri)

        val tempFile = createTempFile(suffix = ".jpg", directory = context.cacheDir)
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
    }
}
