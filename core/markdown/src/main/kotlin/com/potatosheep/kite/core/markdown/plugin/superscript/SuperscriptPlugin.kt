package com.potatosheep.kite.core.markdown.plugin.superscript

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.html.span.SuperScriptSpan
import org.commonmark.parser.Parser
import java.util.Collections

internal class SuperscriptPlugin : AbstractMarkwonPlugin() {

    override fun configureParser(builder: Parser.Builder) {
        builder.extensions(Collections.singleton(SuperscriptExtension()))
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Superscript::class.java) { _, _ ->
            SuperScriptSpan()
        }
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(Superscript::class.java) { visitor, superscript ->
            val length = visitor.length()
            visitor.visitChildren(superscript)
            visitor.setSpansForNodeOptional(superscript, length)
        }
    }

    companion object {
        fun create() = SuperscriptPlugin()
    }
}