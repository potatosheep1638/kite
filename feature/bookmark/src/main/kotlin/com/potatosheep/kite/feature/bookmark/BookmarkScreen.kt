package com.potatosheep.kite.feature.bookmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.common.R.string as commonStrings
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteLoadingIndicator
import com.potatosheep.kite.core.designsystem.KiteSearchBar
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.NoResultsMsg
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider
import com.potatosheep.kite.core.ui.post.PostCard
import kotlinx.coroutines.launch

@Composable
fun BookmarkRoute(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val postListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shouldBlurNsfw by viewModel.blurNsfw.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    BookmarkScreen(
        postListUiState = postListUiState,
        query = query,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onVideoClick = onVideoClick,
        getPostLink = viewModel::getPostLink,
        removeBookmarkedPost = viewModel::removeBookmarkedPost,
        searchSavedPosts = viewModel::searchSavedPosts,
        shouldBlurNsfw = shouldBlurNsfw,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    postListUiState: PostListUiState,
    query: String,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String) -> Unit,
    getPostLink: (Post) -> String,
    removeBookmarkedPost: (Post) -> Unit,
    searchSavedPosts: (String) -> Unit,
    shouldBlurNsfw: Boolean,
    modifier: Modifier = Modifier
) {
    val isLoading = postListUiState is PostListUiState.Loading

    val contentContainerColour =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainerLowest

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    var isSearchBarFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val postsMarkedForDeletion = remember { mutableStateListOf<Post>() }

    DisposableEffect(Unit) {
        onDispose {
            if (postsMarkedForDeletion.isNotEmpty()) {
                postsMarkedForDeletion.forEach {
                    removeBookmarkedPost(it)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            KiteSearchBar(
                query = query,
                backIcon = KiteIcons.Back,
                onBackClick = onBackClick,
                onSearch = {
                    focusManager.clearFocus(true)
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { isSearchBarFocused = it.isFocused },
                onClear = {
                    if (!isSearchBarFocused) {
                        focusRequester.requestFocus()
                        searchSavedPosts("")
                    }
                },
                onQueryChange = {
                    searchSavedPosts(it)

                    coroutineScope.launch {
                        listState.requestScrollToItem(0)
                    }
                },
                inputFieldColors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                colors = SearchBarDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            ) {
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
            when (postListUiState) {
                PostListUiState.Loading -> Unit
                is PostListUiState.Success -> {
                    if (postListUiState.posts.isEmpty()) {
                        NoResultsMsg(
                            title = "Nothing found",
                            subtitle =
                                if (query.isEmpty())
                                    "You have not saved any posts"
                                else
                                    "Try reformulating your query",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    LazyColumn(
                        state = listState
                    ) {
                        itemsIndexed(
                            items = postListUiState.posts,
                            key = { _, post -> "${post.id}/${post.subredditName}" }
                        ) { index, post ->
                            var showPost by rememberSaveable { mutableStateOf(true) }

                            AnimatedVisibility(
                                visible = showPost
                            ) {
                                val msg = stringResource(commonStrings.bookmark_post_removed_msg)
                                val actionLabel = stringResource(commonStrings.undo)

                                PostCard(
                                    post = post,
                                    onClick = {
                                        when {
                                            post.mediaLinks.isEmpty() -> {
                                                onPostClick(post.subredditName, post.id, null, null)
                                            }

                                            post.mediaLinks[0].mediaType == MediaType.GALLERY_THUMBNAIL ||
                                                    post.mediaLinks[0].mediaType == MediaType.ARTICLE_THUMBNAIL ||
                                                    post.mediaLinks[0].mediaType == MediaType.VIDEO_THUMBNAIL -> {

                                                onPostClick(
                                                    post.subredditName,
                                                    post.id,
                                                    null,
                                                    post.mediaLinks[0].link
                                                )
                                            }

                                            else -> {
                                                onPostClick(post.subredditName, post.id, null, null)
                                            }
                                        }
                                    },
                                    onLongClick = {},
                                    onSubredditClick = onSubredditClick,
                                    onUserClick = onUserClick,
                                    onFlairClick = { _, _, _, _ -> },
                                    onImageClick = onImageClick,
                                    onVideoClick = onVideoClick,
                                    onShareClick = { onShare(getPostLink(post), context) },
                                    onBookmarkClick = {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(index)
                                            postsMarkedForDeletion.add(post)
                                            showPost = false

                                            val result = snackbarState.showSnackbar(
                                                message = msg,
                                                actionLabel = actionLabel,
                                                duration = SnackbarDuration.Short
                                            )

                                            when (result) {
                                                SnackbarResult.ActionPerformed -> {
                                                    postsMarkedForDeletion.remove(post)
                                                    showPost = true
                                                }

                                                SnackbarResult.Dismissed -> {
                                                    postsMarkedForDeletion.remove(post)
                                                    removeBookmarkedPost(post)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    showText = false,
                                    blurNsfw = shouldBlurNsfw,
                                    galleryRedirect = true,
                                    isBookmarked = true,
                                    colors = CardDefaults.cardColors(
                                        containerColor = contentContainerColour
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) KiteLoadingIndicator(Modifier.fillMaxSize())
        }
    }
}

@PreviewLightDark
@Composable
fun BookmarkScreenPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        BookmarkScreen(
            postListUiState = PostListUiState.Success(posts),
            query = "",
            onBackClick = {},
            onPostClick = { _, _, _, _ -> },
            onSubredditClick = {},
            onUserClick = {},
            onImageClick = { _, _ -> },
            onVideoClick = {},
            getPostLink = { "" },
            removeBookmarkedPost = {},
            searchSavedPosts = {},
            shouldBlurNsfw = false,
            modifier = Modifier
        )
    }
}