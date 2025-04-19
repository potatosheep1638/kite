package com.potatosheep.kite.core.markdown.converter

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import org.jsoup.Jsoup


class FlexmarkConverter(
    private val flexmarkHtmlConverter: FlexmarkHtmlConverter
) : MarkdownConverter {

    override fun convert(html: String): String {
        val document = Jsoup.parse(html)

        document.select("span.md-spoiler-text")
            .tagName("spoiler")

        document.select("sup")
            .tagName("superscript")

        val conv = flexmarkHtmlConverter
            .convert(document.html())

        val conv2 = conv.replace("<br />", "")
            .replace("!~~~", "!~~ ~") // ensures '~' delimiters in succession work
            .replace("!|||", "!|| |") // same as above but for '|'
            .replace("![](https://", "![]{static-emote}")
            .replace("![](", "")
            .replace(")]", "]")
            .replace("/emote/https://", "![](https://")
            .replace("![]{static-emote}", "![](https://")

        return conv2
    }
}