package com.potatosheep.kite.core.network.client

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.Call
import okhttp3.FormBody
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

        if (isCloudflare) {
            val headerBuilder = Headers.Builder()
            request.requestHeaders.entries.forEach {
                headerBuilder.add(it.key, it.value)
            }
            headers = headerBuilder.build()
            return super.shouldInterceptRequest(view, request)
        }

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
     *
     * Have not yet tested how it handles Cloudflare; may need more work in that regard.
     */
    private fun interceptRequestWithHeaders(request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()
        Log.d("WebViewClient", request.url.toString())

        Log.d("WebViewClient", "HERE!")
        Log.d("WebViewClient", request.method)

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
            Log.d("WebViewClient", request.requestHeaders.toString())
            Log.d("WebViewClient", okHttpRequest.headers.toHeaderList().toString())
            val response = client.newCall(okHttpRequest).execute()

            val mimeType = response.header("Content-Type")?.split(";")?.firstOrNull() ?: "text/html"
            val encoding = response.header("Content-Encoding") ?: "utf-8"

            Log.d("WebViewClient", response.code.toString())

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