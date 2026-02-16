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
    enum class Item { HOME, BUY, INVENTORY, ACCOUNT }

    private val navHome: ImageView
    private val navBuyList: ImageView
    private val navInventory: ImageView
    private val navAccount: ImageView
    lateinit var onClickListener: (Item) -> Unit
    private var currentItem: Item = Item.HOME

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.view_bottom_bar, this)

        navHome = findViewById(R.id.nav_home)
        navBuyList = findViewById(R.id.nav_buy_list)
        navInventory = findViewById(R.id.nav_inventory)
        navAccount = findViewById(R.id.nav_account)

        navHome.setOnClickListener { select(Item.HOME) }
        navBuyList.setOnClickListener { select(Item.BUY) }
        navInventory.setOnClickListener { select(Item.INVENTORY) }
        navAccount.setOnClickListener { select(Item.ACCOUNT) }
    }

    fun select(item: Item) {
        onClickListener.invoke(item)

        currentItem = item

        navHome.isSelected = item == Item.HOME
        navBuyList.isSelected = item == Item.BUY
        navInventory.isSelected = item == Item.INVENTORY
        navAccount.isSelected = item == Item.ACCOUNT

        animateItem(navHome, item == Item.HOME)
        animateItem(navBuyList, item == Item.BUY)
        animateItem(navInventory, item == Item.INVENTORY)
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
