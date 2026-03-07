package com.example.itemly.ui.viewModel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    val mainStack = mutableListOf<String>()
    val detailStack = mutableListOf<Fragment>()
}