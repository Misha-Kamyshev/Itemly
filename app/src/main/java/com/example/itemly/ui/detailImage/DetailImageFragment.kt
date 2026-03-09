package com.example.itemly.ui.detailImage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.itemly.R
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.api.ApiConstants
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.data.model.item.ItemInformation
import com.example.itemly.data.model.item.ItemRequest
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentDetailImageBinding
import com.example.itemly.ui.accountAuthor.AccountAuthor
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.FavoriteViewModel
import com.example.itemly.ui.viewModel.DetailImageViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetailImageFragment : Fragment() {
    private lateinit var data: ItemDataSchema
    private var _binding: FragmentDetailImageBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private lateinit var username: String
    private lateinit var info: ItemInformation

    companion object {
        private const val ARG_ITEM = "arg_item"

        fun newInstance(data: ItemDataSchema): DetailImageFragment {
            return DetailImageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, data)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(ARG_ITEM, ItemDataSchema::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getSerializable(ARG_ITEM) as ItemDataSchema
        }
    }

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
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        username = pref.getString(PrefKeys.USERNAME, "")!!

        setupAdapters()
    }


    private fun setupAdapters() {
        viewLifecycleOwner.lifecycleScope.launch {
            info = getInformation()

            val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

            val headerAdapter = AdapterDetail(
                data,
                username,
                info,
                viewLifecycleOwner,
                favoriteViewModel,
                onClickBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                onClickOther = { view -> onClickOther(view) },
                onClickAuthor = {
                    (activity as? MainActivity)?.openDetailFragment(
                        AccountAuthor(info.author)
                    )
                }
            )

            val adapterImage = AdapterImageView(mutableListOf()) { data ->
                (activity as? MainActivity)?.openDetailFragment(newInstance(data))
            }

            val concatAdapter = ConcatAdapter(headerAdapter, adapterImage)

            binding.recyclerFragmentDetailImage.apply {
                this.adapter = concatAdapter
                this.layoutManager = layout
                this.addItemDecoration(StaggeredGridSpacingItemDecoration(2, 10, true))
            }

            val viewModel = DetailImageViewModel(info.tags)

            subscribeDataForAdapter(
                requireContext(),
                binding.recyclerFragmentDetailImage,
                adapterImage,
                layout,
                viewLifecycleOwner,
                viewModel
            )
        }
    }

    private suspend fun getInformation(): ItemInformation {
        try {
            return ApiClient.apiService.getInformation(data.id, username)
        } catch (_: HttpException) {
            httpToast(context)
            requireActivity().onBackPressedDispatcher.onBackPressed()
            throw CancellationException()
        } catch (_: IOException) {
            ioToast(context)
            requireActivity().onBackPressedDispatcher.onBackPressed()
            throw CancellationException()
        }
    }

    private fun onClickOther(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_detail_image, popupMenu.menu)

        val deleteItem = popupMenu.menu.findItem(R.id.action_delete)

        deleteItem.isVisible = username == info.author

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Вы уверены?")
                        .setMessage("Вы точно хотите удалить изображение для всех?")
                        .setPositiveButton("Да") { dialog, _ -> dialog.dismiss(); onDeleteItem() }
                        .setNeutralButton("Нет", null)
                    true
                }

                R.id.action_download -> {
                    downloadImage()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun onDeleteItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.deleteItem(ItemRequest(data.id, username))
                if (response.isSuccessful) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    httpToast(context)
                }
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }

    private fun downloadImage() {
        Glide.with(requireContext())
            .asBitmap()
            .load(ApiConstants.BASE_URL + data.imageUrl)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    val cause = e?.rootCauses?.firstOrNull()
                    when (cause) {
                        is IOException -> {
                            ioToast(requireContext())
                        }

                        else -> {
                            httpToast(requireContext())
                        }
                    }
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    saveImageToGallery(resource)
                    Toast.makeText(
                        requireContext(),
                        "Сохранено в галерею",
                        Toast.LENGTH_SHORT
                    ).show()
                    return true
                }
            })
            .submit()
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "image_${System.currentTimeMillis()}.jpg"

        val resolver = requireContext().contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Itemly")
        }

        val imageUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        }
    }
}
