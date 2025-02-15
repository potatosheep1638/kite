package com.potatosheep.kite.core.markdown.renderer

import android.content.Context
import okhttp3.OkHttpClient

object Markdown {

    private var markdownRenderer: MarkdownRenderer? = null

    @JvmStatic
    fun markdownRenderer(context: Context): MarkdownRenderer =
        markdownRenderer ?: newMarkdownRenderer(context)

    @Synchronized
    private fun newMarkdownRenderer(context: Context): MarkdownRenderer {
        markdownRenderer?.let { return it }

        val newMarkdownRenderer =
            (context.applicationContext as? MarkdownRendererFactory)?.newMarkdownRenderer()
                ?: MarkdownRenderer(context)

        markdownRenderer = newMarkdownRenderer
        return newMarkdownRenderer
    }
}