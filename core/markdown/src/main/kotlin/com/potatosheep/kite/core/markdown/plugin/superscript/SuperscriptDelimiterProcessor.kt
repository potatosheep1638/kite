package com.potatosheep.kite.core.markdown.plugin.superscript

import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

internal class SuperscriptDelimiterProcessor : DelimiterProcessor {

    override fun getOpeningCharacter(): Char = '^'

    override fun getClosingCharacter(): Char = '^'

    override fun getMinLength(): Int = 1

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() >= 1 && closer.length() >= 1) {
            1
        } else {
            0
        }
    }

    override fun process(opener: Text, closer: Text, delimiterUse: Int) {
        val superscript: Node = Superscript()

        var tmp = opener.next
        while (tmp != null && tmp !== closer) {
            val next = tmp.next
            superscript.appendChild(tmp)
            tmp = next
        }

        opener.insertAfter(superscript)
    }
}