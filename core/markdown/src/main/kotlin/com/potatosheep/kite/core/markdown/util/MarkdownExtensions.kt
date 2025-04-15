package com.potatosheep.kite.core.markdown.util

import android.content.Context
import com.potatosheep.kite.core.markdown.renderer.Markdown
import com.potatosheep.kite.core.markdown.renderer.MarkdownRenderer

internal inline val Context.markdownRenderer: MarkdownRenderer
    get() = Markdown.markdownRenderer(this)

/**
 * Replaces Kite-specific delimiters with their Reddit counterparts (e.g., "||" becomes ">!" or "!<")
 */
fun String.toRedditMarkdown(): String {

    val newString = StringBuilder().append(this)
    var i = 0
    var isPrevCharSpoilerDelimiter = false
    var isOpened = false

    while (i < this.length) {
        when {
            this[i] == '|' && !isPrevCharSpoilerDelimiter -> {
                isPrevCharSpoilerDelimiter = true
            }

            this[i] != '|' && isPrevCharSpoilerDelimiter -> {
                isPrevCharSpoilerDelimiter = false
            }

            this[i] == '|' && isPrevCharSpoilerDelimiter && !isOpened -> {
                newString[i - 1] = '>'
                newString[i] = '!'

                isPrevCharSpoilerDelimiter = false
                isOpened = true
            }

            this[i] == '|' && isPrevCharSpoilerDelimiter && isOpened -> {
                newString[i - 1] = '!'
                newString[i] = '<'

                isPrevCharSpoilerDelimiter = false
                isOpened = false
            }
        }

        i++
    }

    val output = newString.toString().replace("\\|", "|")

    return output
}