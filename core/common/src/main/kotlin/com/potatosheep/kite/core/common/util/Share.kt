package com.potatosheep.kite.core.common.util

import android.content.Context
import android.content.Intent

fun onShare(content: String, context: Context) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)

    context.startActivity(shareIntent)
}