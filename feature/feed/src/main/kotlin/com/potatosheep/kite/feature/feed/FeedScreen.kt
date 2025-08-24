package com.potatosheep.kite.feature.feed

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.LocalTopAppBarActionState
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.ErrorMsg
import com.potatosheep.kite.core.designsystem.KiteBottomSheet
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteLoadingIndicator
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider
import com.potatosheep.kite.core.ui.post.PostCard
import kotlinx.coroutines.launch
import com.potatosheep.kite.core.common.R.string as commonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedRoute(
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onFeedChange: (String?) -> Unit,
    isTitleVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedUiState by viewModel.feedUiState.collectAsStateWithLifecycle()
    val postListUiState by viewModel.postListUiState.collectAsStateWithLifecycle()
    val shouldRefresh by viewModel.shouldRefresh.collectAsStateWithLifecycle()

    FeedScreen(
        feedUiState = feedUiState,
        postListUiState = postListUiState,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onImageClick = onImageClick,
        onUserClick = onUserClick,
        onSearchClick = onSearchClick,
        onVideoClick = onVideoClick,
        onFeedChange = onFeedChange,
        isTitleVisible = isTitleVisible,
        loadSortedPosts = viewModel::loadSortedPosts,
        loadFrontPage = viewModel::loadFrontPage,
        updateFeedSettings = viewModel::updateUiState,
        getPostLink = viewModel::getPostLink,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        modifier = modifier,
        shouldRefresh = shouldRefresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedScreen(
    feedUiState: FeedUiState,
    postListUiState: PostListUiState,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onUserClick: (String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onFeedChange: (String?) -> Unit,
    isTitleVisible: (Boolean) -> Unit,
    loadSortedPosts: (SortOption.Post, SortOption.Timeframe, List<String>, Boolean) -> Unit,
    loadFrontPage: () -> Unit,
    updateFeedSettings: (Feed?, SortOption.Post?, SortOption.Timeframe?) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    getPostLink: (Post) -> String,
    modifier: Modifier = Modifier,
    shouldRefresh: RefreshScope = RefreshScope.NO_REFRESH,
    sortSheetState: SheetState = rememberModalBottomSheetState(),
    feedSheetState: SheetState = rememberModalBottomSheetState()
) {
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val topAppBarActionState = LocalTopAppBarActionState.current
    var showFeedSheet by remember { mutableStateOf(false) }
    var shouldDisableFollowedFeed by rememberSaveable { mutableStateOf(false) }

    when (feedUiState) {
        FeedUiState.Loading -> Unit
        is FeedUiState.Success -> {
            val shouldLoadMorePosts by remember {
                derivedStateOf {
                    !feedUiState.listState.canScrollForward && feedUiState.listState.layoutInfo.totalItemsCount > 0
                }
            }

            val isTitleInView by remember {
                derivedStateOf {
                    feedUiState.listState.firstVisibleItemIndex == 0
                }
            }

            isTitleVisible(isTitleInView)

            if (shouldLoadMorePosts) {
                loadSortedPosts(
                    feedUiState.sort,
                    feedUiState.timeframe,
                    listOf(feedUiState.currentFeed.uri),
                    true
                )
            }

            LaunchedEffect(feedUiState.followedSubreddits) {
                shouldDisableFollowedFeed = feedUiState.followedSubreddits.isEmpty()

                if (shouldDisableFollowedFeed) {
                    updateFeedSettings(Feed.POPULAR, null, null)
                }
            }

            LaunchedEffect(shouldRefresh, feedUiState.currentFeed) {
                when (shouldRefresh) {
                    RefreshScope.FOLLOWED_ONLY -> {
                        if (feedUiState.currentFeed == Feed.FOLLOWED) {
                            loadFrontPage()
                        }
                    }

                    RefreshScope.GLOBAL -> {
                        loadFrontPage()
                    }

                    RefreshScope.NO_REFRESH -> Unit
                }
            }

            when (postListUiState) {
                PostListUiState.Loading -> { KiteLoadingIndicator(Modifier.fillMaxSize()) }
                is PostListUiState.Error -> {
                    ErrorMsg(
                        msg = postListUiState.msg,
                        onRetry = {
                            loadSortedPosts(
                                feedUiState.sort,
                                feedUiState.timeframe,
                                listOf(feedUiState.currentFeed.uri),
                                false
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                is PostListUiState.Success -> {
                    LazyColumn(
                        state = feedUiState.listState,
                        modifier = modifier
                    ) {
                        item {
                            Text(
                                text = stringResource(commonStrings.home_headline),
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp,
                                        top = 48.dp,
                                        end = 12.dp
                                    ),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        item {
                            Text(
                                text = feedUiState.instanceUrl,
                                modifier = Modifier.padding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    bottom = 12.dp
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        item {
                            Row(
                                Modifier.padding(
                                    top = 12.dp,
                                    start = 12.dp,
                                    end = 12.dp,
                                    bottom = 6.dp
                                )
                            ) {
                                FeedSelector(
                                    onClick = { showFeedSheet = true },
                                    currentFeed = feedUiState.currentFeed
                                )

                                PostSorter(
                                    onClick = { topAppBarActionState.showSort = true },
                                    currentSortOption = feedUiState.sort,
                                    currentSortTimeframe = feedUiState.timeframe
                                )
                            }
                        }

                        itemsIndexed(
                            items = postListUiState.posts,
                            /**
                             * Index is appended to the key in case Redlib returns the same post twice,
                             * which results in an [IllegalArgumentException] exception being thrown.
                             */
                            key = { index, post -> "${index}/${post.subredditName}/${post.id}" }
                        ) { _, post ->
                            val onClickFunction = {
                                onPostClick(
                                    post.subredditName,
                                    post.id,
                                    null,
                                    when {
                                        post.mediaLinks.isEmpty() -> null

                                        post.mediaLinks[0].mediaType == MediaType.GALLERY_THUMBNAIL ||
                                                post.mediaLinks[0].mediaType == MediaType.ARTICLE_THUMBNAIL ||
                                                post.mediaLinks[0].mediaType == MediaType.VIDEO_THUMBNAIL -> {

                                            post.mediaLinks[0].link
                                        }

                                        else -> null
                                    }
                                )
                            }

                            var isBookmarked by remember { mutableStateOf(false) }
                            var isChecking by remember { mutableStateOf(true) }

                            LaunchedEffect(Unit) {
                                isBookmarked = checkPostBookmarked(post)
                                isChecking = false
                            }

                            PostCard(
                                post = post,
                                onClick = onClickFunction,
                                onLongClick = {
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            post.title
                                        )
                                    )
                                },
                                onSubredditClick = onSubredditClick,
                                onUserClick = onUserClick,
                                onFlairClick = onSearchClick,
                                onImageClick = onImageClick,
                                onVideoClick = onVideoClick,
                                onShareClick = { onShare(getPostLink(post), context) },
                                onBookmarkClick = {
                                    if (isBookmarked && !isChecking) {
                                        removePostBookmark(post)
                                    } else if (!isChecking) {
                                        bookmarkPost(post)
                                    }

                                    isBookmarked = !isBookmarked
                                },
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                showText = false,
                                blurImage = (feedUiState.blurNsfw && post.isNsfw) ||
                                        (feedUiState.blurSpoiler && post.isSpoiler),
                                isBookmarked = isBookmarked,
                                onSubredditLongClick = { subredditName ->
                                    onSearchClick(
                                        SortOption.Search.RELEVANCE,
                                        SortOption.Timeframe.ALL,
                                        subredditName,
                                        ""
                                    )
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        if (isSystemInDarkTheme())
                                            MaterialTheme.colorScheme.surfaceContainerHigh
                                        else
                                            MaterialTheme.colorScheme.surfaceContainerLowest
                                )
                            )
                        }
                    }
                }
            }

            val coroutineScope = rememberCoroutineScope()

            SortSheet(
                showBottomSheet = topAppBarActionState.showSort,
                sheetState = sortSheetState,
                onDismissRequest = { topAppBarActionState.showSort = false },
                onFilterClick = { sortOption, timeframe ->
                    updateFeedSettings(null, sortOption, timeframe)

                    coroutineScope.launch {
                        feedUiState.listState.requestScrollToItem(0)
                    }

                    loadSortedPosts(
                        sortOption,
                        timeframe,
                        listOf(feedUiState.currentFeed.uri),
                        false
                    )
                },
                currentSortOption = feedUiState.sort,
                currentSortTimeframe = feedUiState.timeframe
            )

            FeedSheet(
                showBottomSheet = showFeedSheet,
                sheetState = feedSheetState,
                onDismissRequest = { showFeedSheet = false },
                onFeedClick = { feed ->
                    updateFeedSettings(feed, null, null)

                    onFeedChange(
                        if (feedUiState.currentFeed == Feed.FOLLOWED)
                            null
                        else
                            feedUiState.currentFeed.uri
                    )

                    coroutineScope.launch {
                        feedUiState.listState.requestScrollToItem(0)
                    }

                    loadSortedPosts(
                        feedUiState.sort,
                        feedUiState.timeframe,
                        listOf(feed.uri),
                        false
                    )
                },
                currentFeed = feedUiState.currentFeed,
                shouldDisableFollowedFeed = shouldDisableFollowedFeed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SortSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onFilterClick: (SortOption.Post, SortOption.Timeframe) -> Unit,
    currentSortOption: SortOption.Post,
    currentSortTimeframe: SortOption.Timeframe,
    modifier: Modifier = Modifier
) {
    KiteBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Text(
            text = stringResource(commonStrings.sort_options),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(Modifier.padding(bottom = 12.dp)) {

            SortOption.Post.entries.forEach { option ->
                val selected = option == currentSortOption

                FilterChip(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            onFilterClick(
                                option,
                                currentSortTimeframe
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(option.label),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }

            if (currentSortOption == SortOption.Post.TOP ||
                currentSortOption == SortOption.Post.CONTROVERSIAL
            ) {

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    thickness = Dp.Hairline
                )

                SortOption.Timeframe.entries.forEach { timeframe ->
                    val selected = timeframe == currentSortTimeframe

                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                onFilterClick(
                                    currentSortOption,
                                    timeframe
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(timeframe.label),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FeedSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    currentFeed: Feed,
    onFeedClick: (Feed) -> Unit,
    modifier: Modifier = Modifier,
    shouldDisableFollowedFeed: Boolean = false
) {

    KiteBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Text(
            text = stringResource(commonStrings.home_feed_sheet_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(Modifier.padding(bottom = 12.dp)) {

            Feed.entries.forEach { option ->
                val selected = option == currentFeed

                FilterChip(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            onFeedClick(option)
                        }
                    },
                    enabled = !(option == Feed.FOLLOWED && shouldDisableFollowedFeed),
                    label = {
                        Text(
                            text = stringResource(option.label),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun FeedSelector(
    onClick: () -> Unit,
    currentFeed: Feed,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        FilterChip(
            selected = currentFeed != Feed.FOLLOWED,
            onClick = onClick,
            label = {
                Text(
                    text = stringResource(currentFeed.label),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                if (currentFeed != Feed.FOLLOWED) {
                    Icon(
                        imageVector = KiteIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector = KiteIcons.DropdownAlt,
                    contentDescription = null
                )
            },
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}

@Composable
private fun PostSorter(
    onClick: () -> Unit,
    currentSortOption: SortOption.Post,
    currentSortTimeframe: SortOption.Timeframe,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        FilterChip(
            onClick = onClick,
            label = {
                Text(
                    text = "${stringResource(currentSortOption.label)} ${
                        if (currentSortOption == SortOption.Post.TOP ||
                            currentSortOption == SortOption.Post.CONTROVERSIAL
                        )
                            " •  ${stringResource(currentSortTimeframe.label)}"
                        else
                            ""
                    }",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            selected = currentSortOption != SortOption.Post.HOT,
            leadingIcon = {
                if (currentSortOption != SortOption.Post.HOT) {
                    Icon(
                        imageVector = KiteIcons.Check,
                        contentDescription = stringResource(currentSortOption.label),
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector = KiteIcons.DropdownAlt,
                    contentDescription = stringResource(commonStrings.content_desc_sort)
                )
            },
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun HomeFeedScreenPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
            FeedScreen(
                postListUiState = PostListUiState.Success(posts),
                feedUiState = FeedUiState.Success(
                    instanceUrl = "https://test.com",
                    followedSubreddits = emptyList(),
                    showNsfw = true,
                    blurNsfw = false,
                    blurSpoiler = false,
                    currentFeed = Feed.FOLLOWED,
                    sort = SortOption.Post.HOT,
                    timeframe = SortOption.Timeframe.DAY,
                    listState = rememberLazyListState()
                ),
                onPostClick = { _, _, _, _ -> },
                onSubredditClick = {},
                onImageClick = { _, _ -> },
                onUserClick = {},
                onSearchClick = { _, _, _, _ -> },
                onVideoClick = {},
                onFeedChange = {},
                isTitleVisible = {},
                loadSortedPosts = { _, _, _, _ -> },
                loadFrontPage = {},
                updateFeedSettings = { _, _, _ -> },
                getPostLink = { _ -> "" },
                checkPostBookmarked = { _ -> false },
                bookmarkPost = {},
                removePostBookmark = {},
            )
        }
    }
}