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

class KiteWebViewClient(private val client: Call.Factory): WebViewClient() {
    private var isCloudflare = false
    private val webViewCookieManager = CookieManager.getInstance();

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? = interceptRequestWithHeaders(request)

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

        // Check if site is protected by cloudflare
        if (webViewCookieManager.getCookie(url).contains("cf_clearance")) {
            isCloudflare =  true
        }

        // Build an OkHttp request
        val okHttpRequest = if (isCloudflare) {
            Log.d("WebViewClient", "HERE!")
            val builder = Request.Builder().url(url)

            val headers = Headers.Builder()
                .add("Accept", "*/*")
                .add("Accept-Encoding", "gzip, deflate, br, zstd")
                .add("Accept-Language", "en-US,en;q=0.5")
                .add("Connection", "keep-alive")
                .add("Content-Type", "text/plain;charset=UTF-8")
                .add("Origin", request.requestHeaders.get("Origin") ?: "")
                .add("Referer", request.requestHeaders.get("Referer") ?: "")
                .add("Sec-Fetch-Dest", "empty")
                .add("Sec-Fetch-Mode", "cors")
                .add("Sec-Fetch-Site", "same-origin")
                .add("Sec-GPC", "1")
                .add("TE", "trailers")
                .add("User-Agent", request.requestHeaders.get("User-Agent") ?: "")

            if (request.method == "POST") {
                val formBody = FormBody.Builder().build()

                builder
                    .headers(
                        headers
                            .add("cf-chl-ra", request.requestHeaders.get("cf-chl-ra") ?: "")
                            .add("cf-chl", request.requestHeaders.get("cf-chl") ?: "")
                            .build()
                    )
                    .method(
                        request.method,
                        formBody
                    )
            } else {
                builder
                    .headers(headers.build())
                    .method(
                        request.method,
                        null
                    )
            }

            builder.build()
        } else {
            Request.Builder()
                .url(url)
                .method(
                    request.method,
                    null
                )
                .build()
        }

        return try {
            Log.d("WebViewClient", request.requestHeaders.toString())
            if (request.method == "POST")
                Log.d("WebViewClient", "POST")

            Log.d("WebViewClient", okHttpRequest.headers.toHeaderList().toString())
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