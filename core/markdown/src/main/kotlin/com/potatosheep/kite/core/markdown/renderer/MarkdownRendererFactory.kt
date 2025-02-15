package com.potatosheep.kite.core.markdown.renderer

interface MarkdownRendererFactory {

    fun newMarkdownRenderer(): MarkdownRenderer
}