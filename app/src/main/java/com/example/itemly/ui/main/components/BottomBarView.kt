package com.example.itemly.ui.main.components

import com.example.itemly.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout

class BottomBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    enum class Item { HOME, FAVORITE, ADD, MY_IMAGE, ACCOUNT }

    private val navHome: ImageView
    private val navFavorite: ImageView
    private val navAdd: ImageView
    private val navMyImage: ImageView
    private val navAccount: ImageView
    lateinit var onClickListener: (Item) -> Unit
    var currentItem: Item = Item.HOME
    var lastCurrentItem: Item? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.view_bottom_bar, this)

        navHome = findViewById(R.id.nav_home)
        navFavorite = findViewById(R.id.nav_favorite)
        navAdd = findViewById(R.id.nav_add)
        navMyImage = findViewById(R.id.nav_my_image)
        navAccount = findViewById(R.id.nav_account)

        navHome.setOnClickListener { select(Item.HOME); onClickListener.invoke(Item.HOME) }
        navFavorite.setOnClickListener { select(Item.FAVORITE); onClickListener.invoke(Item.FAVORITE) }
        navAdd.setOnClickListener { select(Item.ADD); onClickListener.invoke(Item.ADD) }
        navMyImage.setOnClickListener { select(Item.MY_IMAGE); onClickListener.invoke(Item.MY_IMAGE) }
        navAccount.setOnClickListener { select(Item.ACCOUNT); onClickListener.invoke(Item.ACCOUNT) }
    }

    fun select(item: Item) {
        if (item == currentItem && item == Item.ADD) return

        lastCurrentItem = currentItem
        currentItem = item

        navHome.isSelected = item == Item.HOME
        navFavorite.isSelected = item == Item.FAVORITE
        navAdd.isSelected = item == Item.ADD
        navMyImage.isSelected = item == Item.MY_IMAGE
        navAccount.isSelected = item == Item.ACCOUNT

        animateItem(navHome, item == Item.HOME)
        animateItem(navFavorite, item == Item.FAVORITE)
        animateItem(navAdd, item == Item.ADD)
        animateItem(navMyImage, item == Item.MY_IMAGE)
        animateItem(navAccount, item == Item.ACCOUNT)
    }

    private fun animateItem(view: View, selected: Boolean) {
        view.animate()
            .scaleX(if (selected) 1.3f else 1f)
            .scaleY(if (selected) 1.3f else 1f)
            .setDuration(180)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
}
