package com.potatosheep.kite.feature.video

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import com.potatosheep.kite.core.common.R.string as commonStrings
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
    val isHLS by viewModel.isHLS.collectAsStateWithLifecycle()

    val context = LocalContext.current

    VideoScreen(
        videoUiState = uiState,
        player = player,
        isHLS = isHLS,
        onBackClick = onBackClick,
        onShareClick = { onShare(videoLink, context) },
        relaunchPlayer = viewModel::relaunchPlayer,
        releasePlayer = viewModel::releasePlayer,
        pausePlayer = viewModel::pausePlayer,
        download = viewModel::download,
        modifier = modifier
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoScreen(
    videoUiState: VideoUiState,
    player: Player,
    isHLS: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    relaunchPlayer: (Context) -> Unit,
    releasePlayer: () -> Unit,
    pausePlayer: () -> Unit,
    download: (String, Uri, Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val showAppBar = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var hasLaunched by rememberSaveable { mutableStateOf(false) }
    var filename by rememberSaveable { mutableStateOf("") }

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

    val fileWriterLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uri = data?.data

        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            download(filename, it, context)
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
            var showFileNamerDialog by remember { mutableStateOf(false) }
            var showHLSDialog by remember { mutableStateOf(false) }

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
                    onDownloadClick = {
                        if (isHLS)
                            showHLSDialog = true
                        else
                            showFileNamerDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (showFileNamerDialog) {
                FileNamer(
                    onDismissRequest = { showFileNamerDialog = false },
                    onConfirmation = { title ->
                        filename = title
                        fileWriterLauncher.launch(writeIntent())
                        showFileNamerDialog = false
                    },
                    modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                )
            }

            if (showHLSDialog) {
                HLSDialog(
                    onDismissRequest = { showHLSDialog = false },
                    onConfirmation = {
                        showFileNamerDialog = true
                        showHLSDialog = false
                    },
                    modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                )
            }
        }
    }
}

@Composable
private fun FileNamer(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    var currentFileName by rememberSaveable { mutableStateOf("") }
    var isFieldEmpty by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        properties = properties,
        modifier = modifier,
        icon = null,
        title = {
            Text(
                text = stringResource(commonStrings.download),
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            TextField(
                value = currentFileName,
                onValueChange = {
                    currentFileName = it
                    if (isFieldEmpty && currentFileName.isNotEmpty()) isFieldEmpty = false
                },
                label = {
                    Text(
                        text = stringResource(commonStrings.filename),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                supportingText = {
                    if (isFieldEmpty) {
                        Text(
                            text = stringResource(commonStrings.empty_error),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                placeholder = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                isError = isFieldEmpty
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Text(
                text = stringResource(commonStrings.confirm),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        if (currentFileName.isEmpty())
                            isFieldEmpty = true
                        else
                            onConfirmation(currentFileName)
                    },
            )
        },
        dismissButton = null
    )
}

@Composable
private fun HLSDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    val annotatedString = buildAnnotatedString {
        append("${stringResource(commonStrings.hls_warning_body)} ")
        withLink(
            LinkAnnotation.Url(
                url = "https://github.com/potatosheep1638/kite/issues/22",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    hoveredStyle = SpanStyle(MaterialTheme.colorScheme.inversePrimary)
                )
            )
        ) {
            append(stringResource(commonStrings.hls_warning_help_hyperlink))
        }
    }

    AlertDialog(
        properties = properties,
        modifier = modifier,
        icon = null,
        title = {
            Text(
                text = stringResource(commonStrings.hls_warning_title),
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(Modifier) {
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Text(
                text = stringResource(commonStrings.confirm),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onConfirmation() },
            )
        },
        dismissButton = null
    )
}

private fun writeIntent(): Intent {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }

    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

    return intent
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
            isHLS = false,
            onBackClick = {},
            onShareClick = {},
            relaunchPlayer = {},
            pausePlayer = {},
            releasePlayer = {},
            download = { _, _, _ -> }
        )
    }
}