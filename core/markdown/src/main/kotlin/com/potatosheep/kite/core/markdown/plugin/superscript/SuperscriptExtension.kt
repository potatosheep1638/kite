package com.potatosheep.kite.core.markdown.plugin.superscript

import org.commonmark.parser.Parser

internal class SuperscriptExtension : Parser.ParserExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(SuperscriptDelimiterProcessor())
    }

    companion object {
        fun create() = SuperscriptExtension
    }
}