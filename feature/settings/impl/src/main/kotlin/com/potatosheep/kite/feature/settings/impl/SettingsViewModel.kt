package com.potatosheep.kite.feature.settings.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.PostRepository
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
class SettingsViewModel @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    val uiState = userConfigRepository.userConfig
        .map { userConfig ->
            SettingsUiState.Success(
                userConfig = userConfig,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = SettingsUiState.Loading
        )

    private val _instancesUiState = MutableStateFlow<InstancesUiState>(InstancesUiState.Loading)
    val instancesUiState: StateFlow<InstancesUiState> = _instancesUiState

    fun setInstance(instanceUrl: String) {
        viewModelScope.launch {
            userConfigRepository.setInstance(instanceUrl)
        }
    }

    fun setShowNsfw(shouldShow: Boolean) {
        viewModelScope.launch {
            userConfigRepository.setShowNsfw(shouldShow)
        }
    }

    fun setBlurNsfw(shouldBlur: Boolean) {
        viewModelScope.launch {
            userConfigRepository.setNsfwBlur(shouldBlur)
        }
    }

    fun setUseCustomInstance(shouldUse: Boolean) {
        viewModelScope.launch {
            userConfigRepository.setUseCustomInstance(shouldUse)
        }
    }

    fun setCustomInstance(instanceUrl: String) {
        viewModelScope.launch {
            userConfigRepository.setCustomInstance(instanceUrl)
        }
    }

    fun setBlurSpoiler(shouldBlur: Boolean) {
        viewModelScope.launch {
            userConfigRepository.setBlurSpoiler(shouldBlur)
        }
    }

    fun exportBookmarks(uri: Uri, context: Context) {
        viewModelScope.launch {
            postRepository.exportSavedPosts(uri, context)
        }
    }

    fun importBookmarks(uri: Uri, context: Context) {
        viewModelScope.launch {
            postRepository.importSavedPosts(uri, context)
        }
    }

    fun writeDocumentIntent(): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "saved_posts.json")
        }

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        return intent
    }

    fun readDocumentIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        return intent
    }

    fun loadInstances() {
        viewModelScope.launch {
            val instances = userConfigRepository.getInstances()
            _instancesUiState.value = InstancesUiState.Success(instances)
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(
        val userConfig: UserConfig,
    ) : SettingsUiState
}

sealed interface InstancesUiState {
    data object Loading : InstancesUiState

    data class Success(
        val instances: List<String>
    ) : InstancesUiState
}