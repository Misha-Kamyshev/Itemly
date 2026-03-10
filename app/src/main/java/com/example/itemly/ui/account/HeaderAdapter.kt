package com.example.itemly.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.itemly.R
import com.example.itemly.data.api.ApiConstants
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class HeaderAdapter(
    private val username: String,
    private val email: String?,
    private var iconAccountUrl: String?,
    private val userAuthor: Boolean,
    private val onClickPreviewPhoto: () -> Unit,
    private val onClickSetting: () -> Unit = {}
) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: MaterialTextView = view.findViewById(R.id.usernameAccount)
        val email: MaterialTextView = view.findViewById(R.id.emailAccount)
        val icon: ImageView = view.findViewById(R.id.iconAccount)
        val image: ImageView = view.findViewById(R.id.imageAccount)
        val buttonSetting: MaterialCardView = view.findViewById(R.id.buttonSettingsAccount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.block_account_header, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = username
        if (email.isNullOrEmpty()) {
            holder.email.visibility = View.GONE
        } else {
            holder.email.text = email
        }

        if (iconAccountUrl.isNullOrEmpty()) {
            holder.icon.visibility = View.VISIBLE
            holder.image.visibility = View.GONE
        } else {
            holder.icon.visibility = View.GONE
            holder.image.visibility = View.VISIBLE

            Glide.with(holder.image.context)
                .load(ApiConstants.BASE_URL + iconAccountUrl)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.image)
        }

        holder.image.setOnClickListener { onClickPreviewPhoto() }
        holder.icon.setOnClickListener { onClickPreviewPhoto() }

        if (userAuthor) {
            holder.buttonSetting.visibility = View.GONE
        } else {
            holder.buttonSetting.visibility = View.VISIBLE
            holder.buttonSetting.setOnClickListener {
                onClickSetting()
            }
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

    fun updateIcon(newIcon: String?) {
        iconAccountUrl = newIcon
    }
}
