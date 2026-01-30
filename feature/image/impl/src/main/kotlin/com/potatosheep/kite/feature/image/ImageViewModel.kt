package com.potatosheep.kite.feature.image

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.PostRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ImageViewModel.Factory::class)
class ImageViewModel @AssistedInject constructor(
    private val postRepository: PostRepository,
    @Assisted private val imageLinks: List<String>,
    @Assisted private val captions: List<String?>
): ViewModel() {
    private val _uiState = MutableStateFlow<ImageUiState>(ImageUiState.Loading)
    val uiState: StateFlow<ImageUiState> = _uiState
        .apply {
            viewModelScope.launch {
                // Log.i("ImageViewModel", imageLinks.joinToString())
                _uiState.value = ImageUiState.Success(
                    imageLinks,
                    captions,
                )
            }
        }


    fun download(
        imageUrl: String,
        uri: Uri,
        context: Context
    ) {
        viewModelScope.launch {
            runCatching {
                postRepository.downloadImage(
                    url = imageUrl,
                    uri = uri,
                    context = context
                )
            }.onFailure { e ->
                Log.d("ImageViewModel", e.stackTraceToString())
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            imageLinks: List<String>,
            captions: List<String?>
        ): ImageViewModel
    }
}

sealed interface ImageUiState {
    data object Loading : ImageUiState

    data class Success(
        val imageLinks: List<String>,
        val captions: List<String?>
    ) : ImageUiState
}