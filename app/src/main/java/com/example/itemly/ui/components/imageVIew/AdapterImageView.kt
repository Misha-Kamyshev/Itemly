package com.example.itemly.ui.components.imageVIew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.itemly.R
import com.example.itemly.data.model.home.HomeData

class AdapterImageView(
    private val data: List<HomeData>,
    private val onClickItem: (item: HomeData) -> Unit
) :
    RecyclerView.Adapter<AdapterImageView.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_image_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.image.setImageResource(item.imageUrl)
        holder.itemView.setOnClickListener { onClickItem(item) }
    }

    override fun getItemCount(): Int = data.size
}
