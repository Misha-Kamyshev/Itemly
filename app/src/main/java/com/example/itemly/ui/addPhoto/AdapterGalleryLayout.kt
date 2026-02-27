package com.example.itemly.ui.addPhoto

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itemly.R
import com.example.itemly.data.model.addPhoto.DataPhoto
import com.google.android.material.card.MaterialCardView

class AdapterGalleryLayout(
    private val dataGallery: List<DataPhoto>,
    private val onClickImage: (Uri?) -> Unit
) : RecyclerView.Adapter<AdapterGalleryLayout.ViewHolder>() {
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageGalleryPhoto)
        val selectLayout: MaterialCardView = view.findViewById(R.id.selectedLayoutGalleryPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_gallery_photo, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = dataGallery[position]

        Glide.with(holder.itemView)
            .load(photo.uri)
            .centerCrop()
            .into(holder.image)

        holder.selectLayout.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

        holder.image.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if (position == selectedPosition) RecyclerView.NO_POSITION else position

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(position)

            val selectedUri = if (selectedPosition != RecyclerView.NO_POSITION) photo.uri else null
            onClickImage(selectedUri)
        }
    }

    override fun getItemCount(): Int = dataGallery.size
}
