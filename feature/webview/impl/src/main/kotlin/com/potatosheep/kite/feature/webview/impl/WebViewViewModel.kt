package com.potatosheep.kite.feature.webview.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class WebViewViewModel @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {

    val webViewUiState: StateFlow<WebViewUiState> = userConfigRepository.userConfig
        .map { WebViewUiState.Success(it.instance) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WebViewUiState.Loading,
        )
}

sealed interface WebViewUiState {
    data object Loading : WebViewUiState

    data class Success(
        val instance: String
    ) : WebViewUiState
}