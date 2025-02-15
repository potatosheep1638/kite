package com.potatosheep.kite.core.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Singleton


@Singleton
class SimpleCookieJar(cache: List<Cookie>) : CookieJar {
    private val cache = cache.toMutableList()

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val validCookies = mutableListOf<Cookie>()

        cache.forEach { cookie ->
            if (cookie.matches(url)) {
                validCookies.add(cookie)
            }
        }

        return validCookies
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache.addAll(cookies)
    }
}