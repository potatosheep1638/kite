package com.potatosheep.kite.core.markdown.plugin.spoiler

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import org.commonmark.parser.Parser
import java.util.Collections

class SpoilerPlugin : AbstractMarkwonPlugin() {

    override fun configureParser(builder: Parser.Builder) {
        builder.extensions(Collections.singleton(SpoilerExtension.create()))
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Spoiler::class.java) { _, _ ->
            SpoilerSpan()
        }
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(Spoiler::class.java) { visitor, spoiler ->
            val length = visitor.length()
            visitor.visitChildren(spoiler)
            visitor.setSpansForNodeOptional(spoiler, length)
        }
    }

    companion object {
        fun create() = SpoilerPlugin()
    }
}