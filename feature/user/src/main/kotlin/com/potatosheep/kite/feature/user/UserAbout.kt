package com.potatosheep.kite.feature.user

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.potatosheep.kite.core.common.R.string as commonStrings
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.R
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.model.User

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun UserAbout(
    user: User,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                onBackClick = onBackClick,
                title = stringResource(commonStrings.about),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalBackgroundColor.current
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
        },
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )

        ) {
            with (sharedTransitionScope) {
                val sharedBoundsModifier =
                    if (this != null && animatedVisibilityScope != null) {
                        Modifier.sharedElement(
                            state = rememberSharedContentState(key = "about"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    } else {
                        Modifier
                    }

                val scrollState = rememberScrollState()

                val animateIn = remember {
                    MutableTransitionState(false).apply {
                        targetState = true
                    }
                }

                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        if (user.iconLink.isNotBlank()) {
                            AsyncImage(
                                model = user.iconLink,
                                contentDescription = null,
                                modifier = Modifier
                                    .then(sharedBoundsModifier)
                                    .clip(CircleShape)
                                    .size(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = modifier
                                    .then(sharedBoundsModifier)
                                    .clip(CircleShape)
                                    .size(150.dp),
                                contentScale = ContentScale.Crop
                                )
                        }
                    }

                    Text(
                        text = user.userName,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth(),
                    )

                    AnimatedVisibility(visibleState = animateIn) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.padding(vertical = 24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 12.dp,
                                        top = 24.dp,
                                        end = 12.dp,
                                        bottom = 24.dp
                                    )
                            ) {
                                Icon(
                                    imageVector = KiteIcons.Karma,
                                    contentDescription = stringResource(commonStrings.content_desc_karma),
                                    modifier = Modifier.size(26.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Text(
                                    text = "Karma",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(start = 12.dp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                            }

                            Text(
                                text = "${user.karma}",
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Right,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 12.dp, bottom = 24.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }

                    if (user.description.isNotBlank()) {
                        Text(
                            text = user.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            BackHandler {
                onBackClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun UserAboutPreview() {
    val user = User(
        userName = "u/testUser",
        description = "Tactics win battles; strategy wins wars.",
        karma = 12042,
        iconLink = "https://nonsense.link/img/200.jpg"
    )

    KiteTheme {
        UserAbout(
            user = user,
            onBackClick = {}
        )
    }
}