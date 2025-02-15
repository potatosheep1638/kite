package com.potatosheep.kite.core.markdown.converter

import com.vladsch.flexmark.html.renderer.ResolvedLink
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.HtmlConverterExtension
import com.vladsch.flexmark.html2md.converter.HtmlLinkResolver
import com.vladsch.flexmark.html2md.converter.HtmlLinkResolverFactory
import com.vladsch.flexmark.html2md.converter.HtmlMarkdownWriter
import com.vladsch.flexmark.html2md.converter.HtmlNodeConverterContext
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererFactory
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.Collections

fun MarkdownConverter(): FlexmarkConverter {

    val extensionList: Collection<Extension> =
        Collections.singletonList(CustomHtmlConverterExtension.create())

    val options: MutableDataSet = MutableDataSet()
        .set(
            Parser.EXTENSIONS,
            extensionList
        )

    return FlexmarkConverter(
        FlexmarkHtmlConverter
            .builder(options)
            .build()
    )
}

private class CustomHtmlConverterExtension : HtmlConverterExtension {
    override fun rendererOptions(options: MutableDataHolder) {
    }

    override fun extend(builder: FlexmarkHtmlConverter.Builder) {
        builder.linkResolverFactory(EmoteLinkResolver.Factory)
        builder.htmlNodeRendererFactory(CustomHtmlNodeConverter.Factory)
    }

    companion object {
        fun create(): CustomHtmlConverterExtension {
            return CustomHtmlConverterExtension()
        }
    }
}

private class EmoteLinkResolver(context: HtmlNodeConverterContext) : HtmlLinkResolver {

    override fun resolveLink(
        node: Node,
        context: HtmlNodeConverterContext,
        link: ResolvedLink
    ): ResolvedLink {
        return if (link.url.contains("https://.*/emote/".toRegex()))
            link.withUrl("/emote/" + link.url)
        else
            link
    }

    companion object Factory : HtmlLinkResolverFactory {
        override fun apply(context: HtmlNodeConverterContext): HtmlLinkResolver {
            return EmoteLinkResolver(context)
        }

        override fun getAfterDependents(): MutableSet<Class<*>>? {
            return null
        }

        override fun getBeforeDependents(): MutableSet<Class<*>>? {
            return null
        }

        override fun affectsGlobalScope(): Boolean {
            return false
        }
    }
}

private class CustomHtmlNodeConverter(options: DataHolder?) : HtmlNodeRenderer {
    override fun getHtmlNodeRendererHandlers(): MutableSet<HtmlNodeRendererHandler<*>> {
        return mutableSetOf(
            HtmlNodeRendererHandler(
                "spoiler",
                Element::class.java,
                this::processSpoiler
            ),

            HtmlNodeRendererHandler(
                "superscript",
                Element::class.java,
                this::processSuperscript
            )
        )
    }

    private fun processSpoiler(
        node: Element,
        context: HtmlNodeConverterContext,
        out: HtmlMarkdownWriter
    ) {
        context.wrapTextNodes(node, "||", false)
        //context.renderChildren(node, false, null)
    }


    private fun processSuperscript(
        node: Element,
        context: HtmlNodeConverterContext,
        out: HtmlMarkdownWriter
    ) {
        context.wrapTextNodes(node, "^", false)
    }


    companion object Factory : HtmlNodeRendererFactory {
        override fun apply(options: DataHolder?): HtmlNodeRenderer {
            return CustomHtmlNodeConverter(options)
        }
    }
}