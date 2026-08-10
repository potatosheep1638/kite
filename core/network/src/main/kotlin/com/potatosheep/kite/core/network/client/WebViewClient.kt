package com.potatosheep.kite.core.network.client

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.Call
import okhttp3.Headers
import okhttp3.Request
import okhttp3.internal.toHeaderList

class KiteWebViewClient(private val client: Call.Factory, private val cloudflareHeaders: CloudflareHeaders): WebViewClient() {
    private var isCloudflare = false
    private lateinit var headers: Headers
    private val webViewCookieManager = CookieManager.getInstance();

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()
        headers = Headers.Builder().build()

        // Check if site is protected by cloudflare
        val cookie = webViewCookieManager.getCookie(url)
        if ((!cookie.isNullOrEmpty() && cookie.contains("cf_clearance")) ||
            url.contains("cdn-cgi") || url.contains("cloudflare"))
            isCloudflare =  true

        val headerBuilder = Headers.Builder()
        request.requestHeaders.entries.forEach {
            if (it.key != "sec-ch-ua")
                headerBuilder.add(it.key, it.value)
            else
                headerBuilder.add("sec-ch-ua", "\"Not=A?Brand\";v=\"99\",\"Google Chrome\";v=\"151\",\"Chromium\";v=\"151\"")
        }
        if (isCloudflare) {
            headers = headerBuilder.build()
            return super.shouldInterceptRequest(view, request)
        }

        headers = headerBuilder
            //.add("Accept-Encoding", "gzip, deflate, br, zstd")
            .add("Accept-Language", "en-US;q=0.5")
            .build()

        return interceptRequestWithHeaders(request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        cloudflareHeaders.headers = headers
    }

    /*
     * The purpose of this function is to make the request sent to instances match that of one
     * made by an OkHttp client in order to ensure that passing a challenge set by Anubis or go-away
     * in the WebView also passes it for the OkHttp client.
     */
    private fun interceptRequestWithHeaders(request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()

        // Build an OkHttp request
        val okHttpRequest = Request.Builder()
            .url(url)
            .headers(headers)
            .method(
                request.method,
                null
            )
            .build()

        return try {
            val response = client.newCall(okHttpRequest).execute()

            val mimeType = response.header("Content-Type")?.split(";")?.firstOrNull() ?: "text/html"
            val encoding = response.header("Content-Encoding") ?: "utf-8"

            WebResourceResponse(
                mimeType,
                encoding,
                response.body?.byteStream()
            )
        } catch (e: Exception) {
            Log.e("KiteWebViewClient", "${e.message}")
            null
        }
    }
}