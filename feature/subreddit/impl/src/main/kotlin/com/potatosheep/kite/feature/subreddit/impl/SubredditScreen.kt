package com.potatosheep.kite.feature.subreddit.impl

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
import com.potatosheep.kite.core.model.Subreddit

@Composable
fun SubredditRoute(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onUserClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubredditViewModel = hiltViewModel()
) {
    val subredditUiState by viewModel.subredditUiState.collectAsStateWithLifecycle()
    val postUiState by viewModel.postUiState.collectAsStateWithLifecycle()
    val shouldBlurNsfw by viewModel.blurNsfw.collectAsStateWithLifecycle()
    val shouldBlurSpoiler by viewModel.blurSpoiler.collectAsStateWithLifecycle()
    val isSubredditFollowed by viewModel.isSubredditFollowed.collectAsStateWithLifecycle()

    SubredditScreen(
        subredditUiState = subredditUiState,
        postUiState = postUiState,
        shouldBlurNsfw = shouldBlurNsfw,
        shouldBlurSpoiler = shouldBlurSpoiler,
        isSubredditFollowed = isSubredditFollowed,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onImageClick = onImageClick,
        onSearchClick = onSearchClick,
        onUserClick = onUserClick,
        onVideoClick = onVideoClick,
        loadSubreddit = viewModel::loadSubreddit,
        loadSortedPosts = viewModel::loadSortedPosts,
        loadMorePosts = viewModel::loadMorePosts,
        setSubredditFollowed = viewModel::followSubreddit,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        getPostLink = viewModel::getPostLink,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun SubredditScreen(
    subredditUiState: SubredditUiState,
    postUiState: PostUiState,
    shouldBlurNsfw: Boolean,
    shouldBlurSpoiler: Boolean,
    isSubredditFollowed: Boolean,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onUserClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    loadSubreddit: () -> Unit,
    loadSortedPosts: (SortOption.Post, SortOption.Timeframe) -> Unit,
    loadMorePosts: (SortOption.Post, SortOption.Timeframe) -> Unit,
    setSubredditFollowed: (Subreddit, Boolean) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    getPostLink: (Post) -> String,
    modifier: Modifier = Modifier,
) {
    val isLoading = subredditUiState is SubredditUiState.Loading
    var showAbout by rememberSaveable { mutableStateOf(false) }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = showAbout,
            label = "subreddit_screen",
        ) { targetState ->
            when (subredditUiState) {
                SubredditUiState.Loading -> Unit
                is SubredditUiState.Error -> {
                    ErrorMsg(
                        msg = subredditUiState.msg,
                        onRetry = {
                            loadSubreddit()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                is SubredditUiState.Success -> {
                    if (!targetState) {
                        SubredditPostContent(
                            postUiState = postUiState,
                            subreddit = subredditUiState.subreddit,
                            shouldBlurNsfw = shouldBlurNsfw,
                            shouldBlurSpoiler = shouldBlurSpoiler,
                            isSubredditFollowed = isSubredditFollowed,
                            onBackClick = onBackClick,
                            onPostClick = onPostClick,
                            onImageClick = onImageClick,
                            onAboutClick = { showAbout = true },
                            onSearchClick = onSearchClick,
                            onUserClick = onUserClick,
                            onVideoClick = onVideoClick,
                            loadSortedPosts = loadSortedPosts,
                            loadMorePosts = loadMorePosts,
                            setSubredditFollowed = setSubredditFollowed,
                            checkPostBookmarked = checkPostBookmarked,
                            bookmarkPost = bookmarkPost,
                            removePostBookmark = removePostBookmark,
                            getPostLink = getPostLink,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            modifier = modifier
                        )
                    } else {
                        SubredditAboutContent(
                            subreddit = subredditUiState.subreddit,
                            onBackClick = { showAbout = false },
                            modifier = modifier,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }
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
