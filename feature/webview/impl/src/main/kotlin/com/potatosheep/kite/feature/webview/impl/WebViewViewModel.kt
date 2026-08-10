package com.potatosheep.kite.feature.webview.impl

import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    userConfigRepository: UserConfigRepository,
    private val webViewClient: WebViewClient
) : ViewModel() {

    val webViewUiState: StateFlow<WebViewUiState> = userConfigRepository.userConfig
        .map { WebViewUiState.Success(
            if (it.shouldUseCustomInstance)
                it.customInstance
            else
                it.instance,
            webViewClient) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WebViewUiState.Loading,
        )
}

sealed interface WebViewUiState {
    data object Loading : WebViewUiState

    data class Success(
        val instance: String,
        val webViewClient: WebViewClient
    ) : WebViewUiState
}