package com.potatosheep.kite.feature.video.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.potatosheep.kite.core.data.repo.PostRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = VideoViewModel.Factory::class)
class VideoViewModel @AssistedInject constructor(
    videoPlayer: Player,
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    @Assisted private val _videoLink: String
) : ViewModel() {

    val videoLink = savedStateHandle.getStateFlow(VIDEO_LINK, _videoLink)

    val isHLS = savedStateHandle.getStateFlow(IS_HLS, false)

    private val _player = MutableStateFlow(videoPlayer)
    val player: StateFlow<Player> = _player

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState

    fun relaunchPlayer(context: Context) {
        viewModelScope.launch {
            // Log.i("VideoViewModel", _videoLink)
            savedStateHandle[IS_HLS] = checkVideoHLS()

            _player.value = ExoPlayer.Builder(context).build().apply {
                playWhenReady = true
                setMediaItem(
                    MediaItem.Builder()
                        .setUri(_videoLink)
                        .setMimeType(
                            if (isHLS.value)
                                MimeTypes.APPLICATION_M3U8
                            else
                                MimeTypes.VIDEO_MP4
                        )
                        .build()
                )


                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                _uiState.value = VideoUiState.Ended
                            }

                            Player.STATE_READY -> {
                                _uiState.value = VideoUiState.Ready
                            }

                            else -> Unit
                        }
                    }
                })

                prepare()
            }
        }
    }

    fun releasePlayer() {
        viewModelScope.launch {
            _player.value.release()
        }
    }

    fun pausePlayer() {
        viewModelScope.launch {
            _player.value.pause()
        }
    }

    fun download(
        filename: String,
        uri: Uri,
        context: Context
    ) {
        viewModelScope.launch {
            runCatching {
                // Log.d("VideoViewModel", uri.path.toString())
                postRepository.downloadVideo(
                    url = _videoLink,
                    fileName = filename,
                    isHLS = _videoLink.contains("/HLSPlaylist.m3u8"),
                    uri = uri,
                    context = context,
                )
                // Log.d("VideoViewModel", "Done")
            }.onFailure { e ->
                Log.d("VideoViewModel", e.stackTraceToString())
            }
        }
    }

    private fun checkVideoHLS(): Boolean  {
        return _videoLink.contains("/HLSPlaylist.m3u8")
    }

    override fun onCleared() {
        releasePlayer()
    }

    @AssistedFactory
    interface Factory {
        fun create(videoLink: String): VideoViewModel
    }
}

sealed interface VideoUiState {
    data object Loading : VideoUiState

    data object Ready : VideoUiState

    data object Ended : VideoUiState
}

const val VIDEO_LINK = "videoLink"
const val IS_HLS = "isHLS"
