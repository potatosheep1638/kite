package com.potatosheep.kite.core.markdown.plugin.superscript

import org.commonmark.node.CustomNode
import org.commonmark.node.Visitor

internal class Superscript : CustomNode() {

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}