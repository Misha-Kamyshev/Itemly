package com.example.itemly.ui.detailImage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.itemly.R
import com.google.android.material.textview.MaterialTextView

class AdapterTagsDetailImageFragment(
    private val onTagClick: (tag: String) -> Unit
) : RecyclerView.Adapter<AdapterTagsDetailImageFragment.ViewHolder>() {
    private var data: List<String> = emptyList()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tag: MaterialTextView = view.findViewById(R.id.textTagsItemTagsDetailImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_tags_detail_image, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.tag.text = item
        holder.tag.setOnClickListener { onTagClick(item) }
    }

    override fun getItemCount(): Int = data.size

    fun submitData(newData: List<String>) {
        data = newData
        notifyDataSetChanged()
    }
}
