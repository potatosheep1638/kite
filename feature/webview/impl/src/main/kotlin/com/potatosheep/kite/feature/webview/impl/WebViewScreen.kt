package com.potatosheep.kite.feature.webview.impl

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.translation.R.string as Translation

@Composable
fun WebViewRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    val webViewUiState by viewModel.webViewUiState.collectAsStateWithLifecycle()

    WebViewScreen(
        webViewUiState = webViewUiState,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WebViewScreen(
    webViewUiState: WebViewUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = LocalBackgroundColor.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                backIconContentDescription = stringResource(Translation.back),
                onBackClick = onBackClick,
                title = "",
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                ),
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            when (webViewUiState) {
                WebViewUiState.Loading -> Unit
                is WebViewUiState.Success -> {
                    WebView(webViewUiState.instance, webViewUiState.webViewClient)
                }
            }
        }
    }
}

@Composable
fun WebView(url: String, mWebViewClient: WebViewClient){
    val context = LocalContext.current

    AndroidView(factory = {
        WebView(it).apply {
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.0.0 Mobile Safari/537.36"

                // Handle popups properly
                setSupportMultipleWindows(true)

                // Allow zooming
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false

                webViewClient = mWebViewClient
            }
        }
    }, update = {
        it.loadUrl(url)
    })
}

fun getInferredUserAgent(context: Context): String {
    return WebView(context)
        .getDefaultUserAgentString()
        .replace("; Android .*?\\)".toRegex(), "; Android 10; K)")
        .replace("Version/.* Chrome/".toRegex(), "Chrome/")
}

// Based on https://stackoverflow.com/a/29218966
private fun WebView.getDefaultUserAgentString(): String {
    val originalUA: String = settings.userAgentString

    // Next call to getUserAgentString() will get us the default
    settings.userAgentString = null
    val defaultUserAgentString = settings.userAgentString

    // Revert to original UA string
    settings.userAgentString = originalUA

    return defaultUserAgentString
}