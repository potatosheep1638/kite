package com.potatosheep.kite.core.markdown.plugin.spoiler

import org.commonmark.parser.Parser

internal class SpoilerExtension : Parser.ParserExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(SpoilerDelimiterProcessor())
    }

    companion object {
        fun create() = SpoilerExtension()
    }
}