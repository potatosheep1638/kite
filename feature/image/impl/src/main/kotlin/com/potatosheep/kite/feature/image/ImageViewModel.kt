package com.potatosheep.kite.feature.image

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.feature.image.nav.Image
import com.potatosheep.kite.feature.image.nav.ImageParameters
import com.potatosheep.kite.feature.image.nav.ImageParametersType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class ImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository
): ViewModel() {
    private val imagesAndCaptions = savedStateHandle.toRoute<Image>(
        mapOf(typeOf<ImageParameters>() to ImageParametersType)
    ).params

    private val _uiState = MutableStateFlow<ImageUiState>(ImageUiState.Loading)
    val uiState: StateFlow<ImageUiState> = _uiState
        .apply {
            viewModelScope.launch {
                // Log.i("ImageViewModel", imageLinks.joinToString())
                _uiState.value = ImageUiState.Success(
                    imagesAndCaptions.imageLinks,
                    imagesAndCaptions.captions,
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
}

sealed interface ImageUiState {
    data object Loading : ImageUiState

    data class Success(
        val imageLinks: List<String>,
        val captions: List<String?>
    ) : ImageUiState
}