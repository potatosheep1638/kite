package com.potatosheep.kite.core.markdown.renderer

import android.widget.TextView
import io.noties.markwon.Markwon

class MarkwonRenderer(
    private val markwon: Markwon
) : MarkdownRenderer {

    override fun setMarkdown(textView: TextView, input: String) {
        val node = markwon.parse(input)
        val markdown = markwon.render(node)

        markwon.setParsedMarkdown(textView, markdown)
    }
}