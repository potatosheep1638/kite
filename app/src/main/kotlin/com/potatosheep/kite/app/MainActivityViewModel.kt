package com.potatosheep.kite.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.app.MainActivityUiState.Success
import com.potatosheep.kite.app.MainActivityUiState.Loading
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userConfigRepository: UserConfigRepository
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userConfigRepository.userConfig
        .map { Success(it.shouldHideOnboarding) }
        .stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(val shouldHideOnboarding: Boolean) : MainActivityUiState {
        override val shouldShowOnboarding: Boolean get() = !shouldHideOnboarding
    }

    fun shouldKeepSplashScreen() = this is Loading

    val shouldShowOnboarding: Boolean get() = false
}