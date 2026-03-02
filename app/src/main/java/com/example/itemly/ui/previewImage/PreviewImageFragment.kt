package com.example.itemly.ui.previewImage

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.itemly.databinding.FragmentPreviewImageBinding
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.itemly.R
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.objects.PrefKeys
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class PreviewImageFragment : Fragment() {
    private var _binding: FragmentPreviewImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uriString = arguments?.getString("photo_uri")
        val photoUri = uriString?.toUri()

        binding.imageFragmentPreviewImage.setImageURI(photoUri)

        binding.buttonAddTagPreviewImage.setOnClickListener { onClickAddTag() }
        binding.buttonPublishPhoto.setOnClickListener {
            it.isEnabled = false
            binding.layoutPushPhoto.visibility = View.VISIBLE

            it.post {
                dataPreparation()
                binding.layoutPushPhoto.visibility = View.GONE
                it.isEnabled = true
            }
        }

        binding.buttonBackFragmentPreviewImage.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun onClickAddTag() {
        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
        }

        val textInputLayout = TextInputLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            minimumHeight = 50.dp()
            hint = "Тег"
        }

        val editText = TextInputEditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(10.dp())
            }
            minimumHeight = 50.dp()
            textSize = 14f
            maxLines = 1
        }

        val removeButton = MaterialButton(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginStart = 8
                setPadding(0)
            }
            text = ""
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash)
            iconTint = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.black
                )
            )
            iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
            iconSize = 20.dp()
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.main_color)
            )
            setOnClickListener {
                binding.tagsContainer.removeView(rowLayout)
            }
        }

        textInputLayout.addView(editText)
        rowLayout.addView(textInputLayout)
        rowLayout.addView(removeButton)

        binding.tagsContainer.addView(rowLayout)
    }

    private fun dataPreparation() {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)

        val username = pref.getString(PrefKeys.USERNAME, "")!!
        val name = getName()
        val tags = getTags()
        val bitmap = getImage()

        if (tags.isNullOrEmpty() || name.isNullOrEmpty() || bitmap == null)
            return

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val imageBytes = stream.toByteArray()
        val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaType())

        val usernamePart = username.toRequestBody("text/plain".toMediaType())
        val namePart = name.toRequestBody("text/plain".toMediaType())
        val tagsParts = tags.toRequestBody("text/plain".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("image", "photo.jpg", requestFile)

        publicPhoto(usernamePart, namePart, tagsParts, imagePart)
    }

    private fun publicPhoto(
        username: RequestBody,
        name: RequestBody,
        tags: RequestBody,
        image: MultipartBody.Part
    ) {
        lifecycleScope.launch {
            try {
                ApiClient.apiService.addItem(username, name, tags, image)
                Toast.makeText(
                    requireContext(),
                    "Успешно",
                    Toast.LENGTH_LONG
                ).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } catch (e: HttpException) {
                val errorJson = e.response()?.errorBody()?.string()
                    ?: "{\"detail\": \"Ошибка сервера\"}"
                val detail = JSONObject(errorJson).getString("detail")
                Toast.makeText(
                    requireContext(),
                    detail,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка сети попробуйте позже",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getName(): String? {
        val name = binding.titleEditTextFragmentPreviewImage.text?.toString()?.trim()

        if (name.isNullOrEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Ошибка")
                .setMessage("Название не может быть пустым")
                .setPositiveButton("Ок") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return null
        }
        return name
    }

    private fun getTags(): String? {
        var tagsString = ""
        for (i in 0 until binding.tagsContainer.childCount) {
            val row = binding.tagsContainer.getChildAt(0) as LinearLayout
            val textInputLayout = row.getChildAt(0) as TextInputLayout
            val editText = textInputLayout.editText
            val tagText = editText?.text?.toString()?.trim()
            if (!tagText.isNullOrEmpty()) {
                var format = tagText.replace(" ", "").trimStart('#')
                format = "#$format"
                tagsString += format
            }
        }
        tagsString = tagsString.trim()

        if (tagsString.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Ошибка")
                .setMessage("Добавьте хотя-бы один тэг")
                .setPositiveButton("Ок") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return null
        }
        return tagsString
    }

    private fun getImage(): Bitmap? {
        val imageView = binding.imageFragmentPreviewImage
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
        if (bitmap == null) {
            AlertDialog.Builder(requireContext())
                .setTitle("Ошибка")
                .setMessage("Фото не выбрано")
                .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                .show()
            return null
        }
        return bitmap
    }

    private fun Int.dp(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}
