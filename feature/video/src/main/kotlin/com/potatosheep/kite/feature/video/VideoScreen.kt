package com.potatosheep.kite.feature.video

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.MediaTopAppBar

@Composable
fun VideoRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val videoLink by viewModel.videoLink.collectAsStateWithLifecycle()

    val context = LocalContext.current

    VideoScreen(
        videoUiState = uiState,
        player = player,
        onBackClick = onBackClick,
        onShareClick = { onShare(videoLink, context) },
        relaunchPlayer = viewModel::relaunchPlayer,
        releasePlayer = viewModel::releasePlayer,
        pausePlayer = viewModel::pausePlayer,
        modifier = modifier
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoScreen(
    videoUiState: VideoUiState,
    player: Player,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    relaunchPlayer: (Context) -> Unit,
    releasePlayer: () -> Unit,
    pausePlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val showAppBar = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val context = LocalContext.current
    var hasLaunched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasLaunched) {
            relaunchPlayer(context)
            hasLaunched = true
        }
    }

    LifecycleResumeEffect(Unit) {
        onPauseOrDispose {
            pausePlayer()
        }
    }

    Scaffold(
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
                .fillMaxSize()
        ) {
            when (videoUiState) {
                VideoUiState.Loading -> Unit
                VideoUiState.Ready, VideoUiState.Ended -> {
                    key(player) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    setPlayer(player)

                                    showController()

                                    setShowFastForwardButton(false)
                                    setShowRewindButton(false)
                                    setShowPreviousButton(false)
                                    setShowNextButton(false)

                                    setControllerVisibilityListener(
                                        ControllerVisibilityListener { _ ->
                                            if (!isControllerFullyVisible) {
                                                showAppBar.apply { targetState = false }
                                            }
                                        }
                                    )

                                    setOnClickListener { _ ->
                                        if (showAppBar.currentState) {
                                            showAppBar.apply { targetState = false }
                                        } else {
                                            showAppBar.apply { targetState = true }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (videoUiState is VideoUiState.Ended) {
                        showAppBar.apply { targetState = true }
                        view.keepScreenOn = false
                    } else {
                        view.keepScreenOn = true
                    }
                }
            }

            AnimatedVisibility(
                visibleState = showAppBar,
                enter = slideInVertically(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 0
                    )
                ),
                exit = slideOutVertically(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 0
                    )
                ),
            ) {
                MediaTopAppBar(
                    onBackClick = onBackClick,
                    onShareClick = onShareClick,
                    onDownloadClick = {},
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

// TODO: Fix preview
@Preview
@Composable
private fun VideoScreenPreview() {
    val content = LocalContext.current
    val player = ExoPlayer.Builder(content).build().apply {
        prepare()
    }

    KiteTheme {
        VideoScreen(
            videoUiState = VideoUiState.Ready,
            player = player,
            onBackClick = {},
            onShareClick = {},
            relaunchPlayer = {},
            pausePlayer = {},
            releasePlayer = {}
        )
    }
}