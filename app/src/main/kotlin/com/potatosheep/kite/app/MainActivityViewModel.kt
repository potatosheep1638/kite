package com.potatosheep.kite.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.app.MainActivityUiState.Success
import com.potatosheep.kite.app.MainActivityUiState.Loading
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userConfigRepository: UserConfigRepository
) : ViewModel() {
    private val _isColdBoot = MutableStateFlow(true)

    val uiState: StateFlow<MainActivityUiState> = userConfigRepository.userConfig
        .combine(_isColdBoot) { config, boot ->
            Success(config.shouldHideOnboarding, boot)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun isBooted() { _isColdBoot.value = false }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val shouldHideOnboarding: Boolean,
        val isBooted: Boolean
    ) : MainActivityUiState {
        override val shouldShowOnboarding: Boolean get() = !shouldHideOnboarding
        override val isColdBoot: Boolean get() = isBooted
    }

    fun shouldKeepSplashScreen() = this is Loading

    val shouldShowOnboarding: Boolean get() = false

    val isColdBoot: Boolean get() = true
}