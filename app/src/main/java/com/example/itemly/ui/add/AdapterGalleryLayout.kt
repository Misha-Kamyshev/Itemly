package com.example.itemly.ui.add

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itemly.R
import com.example.itemly.data.model.addPhoto.DataPhoto
import com.google.android.material.card.MaterialCardView
import androidx.core.graphics.drawable.toDrawable

class AdapterGalleryLayout(
    private val onClickCamera: () -> Unit,
    private val onClickImage: (Uri?) -> Unit
) : RecyclerView.Adapter<AdapterGalleryLayout.ViewHolder>() {
    private var dataGallery: List<DataPhoto> = emptyList()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageGalleryPhoto)
        val selectLayout: MaterialCardView = view.findViewById(R.id.selectedLayoutGalleryPhoto)
        val camera: ImageView = view.findViewById(R.id.imageCamera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_gallery_photo, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.selectLayout.visibility = View.GONE
        holder.camera.visibility = View.GONE
        holder.image.setImageDrawable(null)
        holder.itemView.background = null

        if (position == 0) {
            holder.itemView.background = ContextCompat.getColor(
                holder.itemView.context,
                android.R.color.black
            ).toDrawable()

            holder.camera.visibility = View.VISIBLE

            holder.itemView.setOnClickListener { onClickCamera() }
            return
        }

        val photo = dataGallery[position - 1]

        Glide.with(holder.itemView)
            .load(photo.uri)
            .centerCrop()
            .into(holder.image)

        holder.selectLayout.visibility =
            if (position == selectedPosition) View.VISIBLE else View.GONE

        holder.image.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition =
                if (position == selectedPosition) RecyclerView.NO_POSITION else position

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(position)

            val selectedUri = if (selectedPosition != RecyclerView.NO_POSITION) photo.uri else null
            onClickImage(selectedUri)
        }
    }

    override fun getItemCount(): Int = dataGallery.size + 1

    fun submitList(newList: List<DataPhoto>) {
        dataGallery = newList
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}