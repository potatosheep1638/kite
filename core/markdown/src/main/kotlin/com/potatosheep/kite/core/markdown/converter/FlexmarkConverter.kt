package com.potatosheep.kite.core.markdown.converter

import android.util.Log
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

        return conv.replace("<br />", "")
            .replace("![](https://", "![]{static-emote}")
            .replace("![](", "")
            .replace(")]", "]")
            .replace("/emote/https://", "![](https://")
            .replace("![]{static-emote}", "![](https://")
    }
}