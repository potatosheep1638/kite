package com.potatosheep.kite.feature.image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animatePanBy
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.MediaTopAppBar
import com.potatosheep.kite.core.ui.DynamicAsyncImage
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import com.potatosheep.kite.core.translation.R.string as Translation

@Composable
fun ImageRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImageViewModel = hiltViewModel()
) {
    val imageUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ImageScreen(
        imageUiState = imageUiState,
        onBackClick = onBackClick,
        onShareClick = { onShare(it, context) },
        download = viewModel::download,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageScreen(
    imageUiState: ImageUiState,
    onBackClick: () -> Unit,
    onShareClick: (String) -> Unit,
    download: (String, Uri, Context) -> Unit,
    modifier: Modifier = Modifier,
    previewMode: Boolean = false
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showAppBar by remember { mutableStateOf(true) }

    val view = LocalView.current
    val window = (view.context as Activity).window
    val insetsController = WindowCompat.getInsetsController(window, view)

    val coroutineScope = rememberCoroutineScope()

    val scale = remember { mutableFloatStateOf(1f) }
    val offsetX = remember { mutableFloatStateOf(0f) }
    val offsetY = remember { mutableFloatStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val transformableState = rememberTransformableState { zoom, pan, _ ->
        scale.floatValue = maxOf(1f, minOf(scale.floatValue * zoom, 3f))

        val maxX = (size.width * (scale.floatValue - 1)) / 2
        val maxY = (size.height * (scale.floatValue - 1)) / 2

        offsetX.floatValue =
            maxOf(-maxX, minOf(maxX, offsetX.floatValue + (pan.x * (scale.floatValue / 3f))))
        offsetY.floatValue = maxOf(-maxY, minOf(maxY, offsetY.floatValue + pan.y))
    }

    var currentImageLink by remember { mutableStateOf("") }

    if (showAppBar) {
        insetsController.apply {
            show(WindowInsetsCompat.Type.statusBars())
            show(WindowInsetsCompat.Type.navigationBars())
        }
    } else {
        insetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
        }
    }

    val fileWriterLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uri = data?.data

        uri?.let { download(currentImageLink, uri, context) }
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
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showAppBar = !showAppBar
                        },
                        onDoubleTap = {
                            if (scale.floatValue == 1f) {
                                coroutineScope.launch {
                                    transformableState.animateZoomBy(2f, tween(150))
                                }
                            } else {
                                coroutineScope.launch {
                                    transformableState.animatePanBy(Offset(0f, 0f), tween(10))
                                    transformableState.animateZoomBy(
                                        1 / scale.floatValue,
                                        tween(150)
                                    )
                                    scale.floatValue = 1f
                                    offsetX.floatValue = 0f
                                    offsetY.floatValue = 0f
                                }
                            }
                        }
                    )
                }
        ) {
            when (imageUiState) {
                ImageUiState.Loading -> Unit
                is ImageUiState.Success -> {
                    val pagerState = rememberPagerState(pageCount = {
                        if (!previewMode)
                            imageUiState.imageLinks.size
                        else
                            3
                    })

                    HorizontalPager(
                        state = pagerState
                    ) { page ->
                        currentImageLink = imageUiState.imageLinks[page]

                        Box(Modifier.fillMaxSize()) {
                            DynamicAsyncImage(
                                model = imageUiState.imageLinks[page],
                                contentDescription = null,
                                placeholder =
                                if (page == 0 && previewMode)
                                    painterResource(id = R.drawable.ic_launcher_background)
                                else if (page == 1 && previewMode)
                                    painterResource(id = R.drawable.ic_launcher_foreground)
                                else
                                    null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(
                                        vertical = if (previewMode) 200.dp else Dp.Unspecified
                                    )
                                    .onSizeChanged {
                                        size = it
                                    }
                                    .pointerInput(Unit) {
                                        detectTransformGesturesWithChanges(
                                            lockZoom = { pagerState.isScrollInProgress }
                                        ) { _, pan, zoom, _, changes ->
                                            scale.floatValue =
                                                maxOf(1f, minOf(scale.floatValue * zoom, 3f))

                                            val maxX = (size.width * (scale.floatValue - 1)) / 2
                                            val maxY = (size.height * (scale.floatValue - 1)) / 2

                                            offsetX.floatValue = maxOf(
                                                -maxX,
                                                minOf(
                                                    maxX,
                                                    offsetX.floatValue + (pan.x * (scale.floatValue / 3f))
                                                )
                                            )
                                            offsetY.floatValue =
                                                maxOf(
                                                    -maxY,
                                                    minOf(maxY, offsetY.floatValue + pan.y)
                                                )

                                            // If image is not zoomed out OR if more than one pointer is down,
                                            // the pager will not scroll.
                                            if ((scale.floatValue != 1f) || changes.size > 1) {
                                                changes.fastForEach {
                                                    it.consume()
                                                }
                                            }
                                        }
                                    }
                                    .graphicsLayer {
                                        scaleX = scale.floatValue
                                        scaleY = scale.floatValue
                                        translationX = offsetX.floatValue
                                        translationY = offsetY.floatValue
                                    }
                            )

                            AnimatedVisibility(
                                visible = showAppBar && !imageUiState.captions[page].isNullOrBlank(),
                                modifier = Modifier.align(Alignment.BottomCenter),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Card(
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                        bottom = 24.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = LocalBackgroundColor.current
                                    )
                                ) {
                                    val captionCopiedToastText = stringResource(Translation.copied_clipboard)

                                    Text(
                                        text = imageUiState.captions[page] ?: "",
                                        modifier = Modifier
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                            .clickable {
                                                clipboardManager.setText(
                                                    AnnotatedString(
                                                        imageUiState.captions[page] ?: ""
                                                    )
                                                )

                                                Toast.makeText(
                                                    context,
                                                    captionCopiedToastText,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showAppBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MediaTopAppBar(
                    onBackClick = onBackClick,
                    onShareClick = { onShareClick(currentImageLink) },
                    onDownloadClick = { fileWriterLauncher.launch(writeIntent(currentImageLink)) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        }
    }
}

suspend fun PointerInputScope.detectTransformGesturesWithChanges(
    lockZoom: () -> Boolean,
    panZoomLock: Boolean = false,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float, changes: List<PointerInputChange>) -> Unit
) {
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        awaitFirstDown(requireUnconsumed = false)
        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.fastAny { it.isConsumed }
            if (!canceled) {
                val zoomChange = if (!lockZoom()) event.calculateZoom() else 1f
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                    val panMotion = pan.getDistance()

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop ||
                        panMotion > touchSlop
                    ) {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                    if (effectiveRotation != 0f ||
                        zoomChange != 1f ||
                        panChange != Offset.Zero
                    ) {
                        onGesture(centroid, panChange, zoomChange, effectiveRotation, event.changes)
                    }
                }
            }
        } while (!canceled && event.changes.fastAny { it.pressed })
    }
}

private fun writeIntent(imageUrl: String): Intent {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = getMimeType(getExtension(imageUrl))
        putExtra(Intent.EXTRA_TITLE, getImageName(imageUrl))
    }

    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

    return intent
}

private fun getImageName(imageUrl: String): String {
    val imageName = imageUrl.substringBefore("?")
        .substringAfterLast("/")

    return imageName
}

private fun getExtension(imageUrl: String): String {
    val imageExtension = imageUrl.substringBefore("?")
        .split(".")
        .last()

    return imageExtension
}

private fun getMimeType(extension: String): String {
    return if (extension == "jpg")
        "image/jpeg"
    else
        "image/$extension"
}

// TODO: Make a 'preview mode'
@Preview
@Composable
private fun ImageScreenPreview() {
    val dummyImageLinks = listOf(
        "test",
        "test",
        "test"
    )

    val dummyImageCaptions = listOf(
        "test caption",
        null,
        "test caption"
    )

    KiteTheme {
        Surface {
            ImageScreen(
                imageUiState = ImageUiState.Success(dummyImageLinks, dummyImageCaptions),
                onBackClick = {},
                onShareClick = {},
                download = { _, _, _ -> },
                previewMode = true
            )
        }
    }
}