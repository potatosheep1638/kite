package com.potatosheep.kite.core.markdown.plugin.spoiler

import android.graphics.Color
import android.text.Spannable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import io.noties.markwon.utils.ColorUtils

internal class SpoilerSpan : ClickableSpan() {

    private var shown: Boolean = false

    override fun onClick(view: View) {
        if (view !is TextView) {
            return
        }

        val spannable = (view.text as Spannable)

        val end = spannable.getSpanEnd(this)
        if (end < 0) {
            return
        }

        view.layout ?: return

        shown = !shown
        view.invalidate()
    }

    override fun updateDrawState(ds: TextPaint) {

        if (shown) {
            ds.bgColor = ColorUtils.applyAlpha(ds.color, 25)
        } else {
            ds.bgColor = ds.color
        }

        ds.isUnderlineText = false
    }
}