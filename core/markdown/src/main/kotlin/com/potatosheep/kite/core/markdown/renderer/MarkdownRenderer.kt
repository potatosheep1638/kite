package com.potatosheep.kite.core.markdown.renderer

import android.widget.TextView

interface MarkdownRenderer {

    fun setMarkdown(textView: TextView, input: String)
}