package com.potatosheep.kite.core.network

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Singleton


@Singleton
class SimpleCookieJar : CookieJar {
    private val webViewCookieManager = CookieManager.getInstance();

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val validCookies = webViewCookieManager.getCookie(url.toString())

        return if (!validCookies.isNullOrEmpty()) {
            val cookie = validCookies.split(";").mapNotNull { Cookie.parse(url, it) }
            cookie
        } else {
            emptyList()
        }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val urlString = url.toString()

        cookies.forEach { webViewCookieManager.setCookie(urlString, it.toString()) }
    }
}
