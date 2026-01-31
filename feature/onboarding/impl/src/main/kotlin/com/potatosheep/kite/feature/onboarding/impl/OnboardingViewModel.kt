package com.potatosheep.kite.feature.onboarding.impl

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.UserConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userConfigRepository: UserConfigRepository
): ViewModel() {

    val onboardingUiState = userConfigRepository.userConfig
        .map(OnboardingUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = OnboardingUiState.Loading
        )

    private val _instancesUiState = MutableStateFlow<InstancesUiState>(InstancesUiState.Loading)
    val instancesUiState: StateFlow<InstancesUiState> = _instancesUiState

    fun getInstances() {
        viewModelScope.launch {
            val instances = userConfigRepository.getInstances()
            _instancesUiState.value = InstancesUiState.Success(instances)
        }
    }

    fun setInstance(instanceUrl: String) {
        viewModelScope.launch {
            userConfigRepository.setInstance(instanceUrl)
        }
    }

    fun setCustomInstance(instanceUrl: String) {
        viewModelScope.launch {
            val cleanedUrl =
                if (instanceUrl.last() == '/')
                    instanceUrl.substringBeforeLast("/")
                else
                    instanceUrl

            userConfigRepository.setCustomInstance(cleanedUrl)
        }
    }

    fun setUseCustomInstance(useCustomInstance: Boolean) {
        viewModelScope.launch {
            userConfigRepository.setUseCustomInstance(useCustomInstance)
        }
    }

    fun setShouldOnboard(shouldOnboard: Boolean) {
        viewModelScope.launch {
            userConfigRepository.setOnboarding(shouldOnboard)
        }
    }

    fun validateUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}

sealed interface OnboardingUiState {
    data object Loading : OnboardingUiState

    data class Success(
        val userConfig: UserConfig
    ): OnboardingUiState
}

sealed interface InstancesUiState {
    data object Loading : InstancesUiState

    data class Success(
        val instances: List<String>
    ) : InstancesUiState
}