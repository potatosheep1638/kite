package com.potatosheep.kite.core.network.util

import android.util.Log
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun Response.parseHtml(): Document = use {
    // Log.i("ParserNetwork",request.url.toString())
    val body = requireNotNull(body)
    val charset = body.contentType()?.charset()?.name()
    Jsoup.parse(body.byteStream(), charset, request.url.toString())
}
