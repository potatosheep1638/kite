package com.potatosheep.kite.feature.webview.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object WebViewNavKey : NavKey

fun Navigator.navigateToWebView() {
    navigate(WebViewNavKey)
}