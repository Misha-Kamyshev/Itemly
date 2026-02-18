package com.example.itemly.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.itemly.data.model.DataSpanConfig

fun buildColoredSpannable(
    context: Context,
    fullText: String,
    spans: List<DataSpanConfig>
): SpannableString {
    val spannable = SpannableString(fullText)

    spans.forEach { config ->
        val color = ContextCompat.getColor(context, config.colorRes)
        spannable.setSpan(
            ForegroundColorSpan(color),
            config.start,
            config.end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannable
}
