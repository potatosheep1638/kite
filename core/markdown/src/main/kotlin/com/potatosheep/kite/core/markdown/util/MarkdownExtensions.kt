package com.potatosheep.kite.core.markdown.util

import android.content.Context
import com.potatosheep.kite.core.markdown.renderer.Markdown
import com.potatosheep.kite.core.markdown.renderer.MarkdownRenderer

internal inline val Context.markdownRenderer: MarkdownRenderer
    get() = Markdown.markdownRenderer(this)