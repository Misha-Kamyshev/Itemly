package com.example.itemly.utils

import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.nextFocus(next: TextInputEditText? = null, onDone: (() -> Unit)? = null) {
    setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_NEXT -> {
                next?.requestFocus()
                true
            }
            EditorInfo.IME_ACTION_DONE -> {
                onDone?.invoke()
                true
            }
            else -> false
        }
    }
}
