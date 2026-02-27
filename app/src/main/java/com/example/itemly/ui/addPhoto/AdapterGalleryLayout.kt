package com.example.itemly.ui.addPhoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itemly.R
import com.example.itemly.data.model.addPhoto.DataPhoto

class AdapterGalleryLayout(
    private val dataGallery: List<DataPhoto>
) : RecyclerView.Adapter<AdapterGalleryLayout.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageGalleryPhoto)
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
    }

    override fun getItemCount(): Int = dataGallery.size
}
