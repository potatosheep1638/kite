package com.potatosheep.kite.core.network.client

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.potatosheep.kite.core.network.util.parseHtml
import okhttp3.Call
import okhttp3.Request

class KiteWebViewClient(private val client: Call.Factory): WebViewClient() {
    private var isCloudflare = false

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        Log.d("WebViewClient", request.url.toString())
        val requestUrl = request.url.toString()

        // We do not want to intercept cloudflare challenges with OkHttp
        if (requestUrl.contains("cdn-cgi") ||
            requestUrl.contains("cloudflare") ||
            isCloudflare) {

            isCloudflare =  true
            return super.shouldInterceptRequest(view, request)
        }

        return interceptRequestWithHeaders(request)
    }

    /*
     * The purpose of this function is to make the request sent to instances match that of one
     * made by an OkHttp client in order to ensure that passing a challenge set by Anubis or go-away
     * in the WebView also passes it for the OkHttp client.
     *
     * Have not yet tested how it handles Cloudflare; may need more work in that regard.
     */
    private fun interceptRequestWithHeaders(request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()

        return try {
            // Build an OkHttp request
            val okHttpRequest = Request.Builder()
                .url(url)
                .method(
                    request.method,
                    null
                )
                .build()

            val response = client.newCall(okHttpRequest).execute()

            // Check if WebView is still on Cloudflare
            if (isCloudflare &&
                !url.contains("cloudflare") &&
                response.parseHtml().select("title").text() != "Just a moment...") {
                isCloudflare = false
            }

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