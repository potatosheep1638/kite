package com.potatosheep.kite.feature.user

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.designsystem.ErrorMsg
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.model.Post

@Composable
fun UserRoute(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel()
) {
    val userUiState by viewModel.userUiState.collectAsStateWithLifecycle()
    val postUiState by viewModel.userFeedUiState.collectAsStateWithLifecycle()
    val shouldBlurNsfw by viewModel.blurNsfw.collectAsStateWithLifecycle()
    val shouldBlurSpoiler by viewModel.blurSpoiler.collectAsStateWithLifecycle()

    UserScreen(
        userUiState = userUiState,
        userFeedUiState = postUiState,
        shouldBlurNsfw = shouldBlurNsfw,
        shouldBlurSpoiler = shouldBlurSpoiler,
        loadSortedPostsAndComments = viewModel::loadSortedPostsAndComments,
        loadMorePostsAndComments = viewModel::loadMorePostsAndComments,
        getPostLink = viewModel::getPostLink,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onImageClick = onImageClick,
        onFlairClick = onFlairClick,
        onVideoClick = onVideoClick,
        loadUser = viewModel::loadUser,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun UserScreen(
    userUiState: UserUiState,
    userFeedUiState: UserFeedUiState,
    shouldBlurNsfw: Boolean,
    shouldBlurSpoiler: Boolean,
    loadSortedPostsAndComments: (sortOption: SortOption.User) -> Unit,
    loadMorePostsAndComments: (sortOption: SortOption.User) -> Unit,
    getPostLink: (Post) -> String,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    loadUser: () -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = userUiState is UserUiState.Loading
    var showAbout by rememberSaveable { mutableStateOf(false) }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = showAbout,
            label = "user_screen",
        ) { targetState ->
            when (userUiState) {
                UserUiState.Loading -> Unit
                is UserUiState.Success -> {
                    if (!targetState) {
                        UserContent(
                            user = userUiState.user,
                            userFeedUiState = userFeedUiState,
                            shouldBlurNsfw = shouldBlurNsfw,
                            shouldBlurSpoiler = shouldBlurSpoiler,
                            loadSortedPostsAndComments = loadSortedPostsAndComments,
                            loadMorePostsAndComments = loadMorePostsAndComments,
                            getPostLink = getPostLink,
                            onBackClick = onBackClick,
                            onPostClick = onPostClick,
                            onSubredditClick = onSubredditClick,
                            onImageClick = onImageClick,
                            onAboutClick = { showAbout = true },
                            onFlairClick = onFlairClick,
                            onVideoClick = onVideoClick,
                            checkPostBookmarked = checkPostBookmarked,
                            bookmarkPost = bookmarkPost,
                            removePostBookmark = removePostBookmark,
                            modifier = modifier,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    } else {
                        UserAbout(
                            user = userUiState.user,
                            onBackClick = { showAbout = false },
                            modifier = modifier,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }
                }

                is UserUiState.Error -> {
                    ErrorMsg(
                        msg = userUiState.msg,
                        onRetry = {
                            loadUser()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }
            }
        }

        if (isLoading) {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        backIcon = KiteIcons.Back,
                        onBackClick = onBackClick,
                        title = "",
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = LocalBackgroundColor.current
                        )
                    )
                },
                containerColor = LocalBackgroundColor.current,
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { padding ->
                Box(
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal
                            )
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            Modifier.width(64.dp)
                        )
                    }
                }
            }
        }
    }
}