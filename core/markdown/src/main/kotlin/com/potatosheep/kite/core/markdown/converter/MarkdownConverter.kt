package com.potatosheep.kite.core.markdown.converter

interface MarkdownConverter {

    fun convert(html: String): String
}