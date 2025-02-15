package com.potatosheep.kite.core.markdown.plugin.spoiler

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

internal class Spoiler : CustomNode(), Delimited {

    override fun getOpeningDelimiter(): String = DELIMITER

    override fun getClosingDelimiter(): String = DELIMITER

    companion object {
        const val DELIMITER: String = "||"
    }
}