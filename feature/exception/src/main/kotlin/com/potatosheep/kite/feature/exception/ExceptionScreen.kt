package com.potatosheep.kite.feature.exception

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import java.io.IOException
import com.potatosheep.kite.core.translation.R.string as Translation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExceptionRoute(
    throwable: Throwable,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExceptionViewModel = hiltViewModel()
) {
    val charmony by viewModel.charmony.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    ExceptionScreen(
        throwable = throwable,
        charmony = charmony,
        onBackClick = onBackClick,
        modifier = modifier,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExceptionScreen(
    throwable: Throwable,
    charmony: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {

    val horiScrollState = rememberScrollState()
    val vertScrollState = rememberScrollState()
    var showBrainRot by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "",
                backIcon = KiteIcons.Back,
                backIconContentDescription = stringResource(Translation.back),
                onBackClick = onBackClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalBackgroundColor.current
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
        },
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(padding)
                .padding(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
                .verticalScroll(vertScrollState),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Translation.exception_headline),
                modifier = Modifier
                    .padding(
                        top = 24.dp,
                        start = 12.dp,
                        end = 12.dp,
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )

            val transitionState = remember {
                MutableTransitionState(false).apply {
                    targetState = true
                }
            }

            Box(
                modifier = Modifier
                    .heightIn(400.dp, Dp.Unspecified)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visibleState = transitionState,
                    enter = scaleIn(
                        tween(
                            durationMillis = 400
                        )
                    )
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.n113642dvrpd1),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .clip(CircleShape)
                            .size(300.dp)
                            .clickable {
                                showBrainRot = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn(
                    animationSpec = tween(
                        delayMillis = 450
                    )
                )
            ) {
                Text(
                    text = stringResource(Translation.exception_title),
                    modifier = Modifier
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                        )
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn(
                    animationSpec = tween(
                        delayMillis = 500
                    )
                )
            ) {
                Text(
                    text = stringResource(Translation.exception_subtitle),
                    modifier = Modifier
                        .padding(
                            top = 2.dp,
                            start = 12.dp,
                            end = 12.dp,
                        )
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn(
                    animationSpec = tween(
                        delayMillis = 550
                    )
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 24.dp
                        )
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    Row(
                        Modifier
                            .padding(12.dp)
                            .horizontalScroll(horiScrollState)
                    ) {
                        SelectionContainer {
                            Text(
                                text = throwable.stackTraceToString(),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines =
                                if (expanded)
                                    Int.MAX_VALUE
                                else
                                    5,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    if (throwable.stackTrace.size > 5) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    bottom = 12.dp
                                )
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "See ${
                                    if (expanded)
                                        "less"
                                    else
                                        "more"
                                }",
                                modifier = Modifier.clickable { expanded = !expanded },
                                fontFamily = KiteFonts.InterMedium,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Icon(
                                imageVector =
                                if (expanded)
                                    KiteIcons.Collapse
                                else
                                    KiteIcons.DropdownAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            if (showBrainRot) {
                val configuration = LocalConfiguration.current

                PureEvilDialog(
                    text = charmony,
                    onDismissRequest = { showBrainRot = false },
                    onConfirmation = { showBrainRot = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp)
                )
            }
        }
    }
}

@Composable
private fun PureEvilDialog(
    text: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        properties = properties,
        modifier = modifier,
        icon = null,
        title = {
            Text(
                text = "Charmony",
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                Modifier
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Text(
                text = "...",
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ExceptionScreenPreview() {
    KiteTheme {
        Surface {
            ExceptionScreen(
                charmony = "",
                throwable = IOException("Random IO exception go!"),
                onBackClick = {}
            )
        }
    }
}
