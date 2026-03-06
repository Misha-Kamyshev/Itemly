package com.example.itemly.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.itemly.R
import com.example.itemly.data.api.ApiConstants
import com.google.android.material.textview.MaterialTextView

class HeaderAdapter(
    private val username: String,
    private val email: String,
    private val iconAccountUrl: String?
) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: MaterialTextView = view.findViewById(R.id.usernameAccount)
        val email: MaterialTextView = view.findViewById(R.id.emailAccount)
        val icon: ImageView = view.findViewById(R.id.imageAccount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.block_account_header, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = username
        holder.email.text = email

        if (iconAccountUrl.isNullOrEmpty()) {
            holder.icon.setImageResource(R.drawable.ic_account)
        } else {
            Glide.with(holder.icon.context)
                .load(ApiConstants.BASE_URL + iconAccountUrl)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(holder.icon)
        }
    }

    override fun getItemCount(): Int = 1

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)

        val params = holder.itemView.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = true
        }
    }
}
