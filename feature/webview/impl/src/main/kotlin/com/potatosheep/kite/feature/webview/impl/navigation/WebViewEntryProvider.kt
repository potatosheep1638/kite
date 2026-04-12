package com.potatosheep.kite.feature.webview.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.webview.api.navigation.WebViewNavKey
import com.potatosheep.kite.feature.webview.impl.WebViewRoute

fun EntryProviderScope<NavKey>.webViewEntry(navigator: Navigator) {
    entry<WebViewNavKey>(metadata = defaultTransitionSpec()) {
        WebViewRoute({ navigator.goBack() })
    }
}