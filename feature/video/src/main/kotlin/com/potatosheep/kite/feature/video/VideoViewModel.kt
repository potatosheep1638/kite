package com.potatosheep.kite.feature.video

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.toRoute
import com.potatosheep.kite.feature.video.nav.VideoRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    videoPlayer: Player
) : ViewModel() {

    private val _videoLink = savedStateHandle.toRoute<VideoRoute>().videoLink
    val videoLink = savedStateHandle.getStateFlow(VIDEO_LINK, _videoLink)

    private val _player = MutableStateFlow(videoPlayer)
    val player: StateFlow<Player> = _player

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState

    fun relaunchPlayer(context: Context) {
        viewModelScope.launch {
            // Log.i("VideoViewModel", _videoLink)

            _player.value = ExoPlayer.Builder(context).build().apply {
                playWhenReady = true
                setMediaItem(
                    MediaItem.Builder()
                        .setUri(_videoLink)
                        .setMimeType(
                            if (_videoLink.contains("hls"))
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

    override fun onCleared() {
        releasePlayer()
    }
}

sealed interface VideoUiState {
    data object Loading : VideoUiState

    data object Ready : VideoUiState

    data object Ended : VideoUiState
}

const val VIDEO_LINK = "videoLink"