package com.potatosheep.kite.feature.homefeed

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.potatosheep.kite.core.common.enums.MenuOption
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.ErrorMsg
import com.potatosheep.kite.core.designsystem.KiteBottomSheet
import com.potatosheep.kite.core.designsystem.KiteDropdownMenu
import com.potatosheep.kite.core.designsystem.KiteDropdownMenuItem
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteLoadingIndicator
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.KiteTopAppBar
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
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
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    navBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val homeFeedUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val blurNsfw by viewModel.blurNsfw.collectAsStateWithLifecycle()
    val blurSpoiler by viewModel.blurSpoiler.collectAsStateWithLifecycle()
    val instance by viewModel.instanceUrl.collectAsStateWithLifecycle()
    val followedSubreddits by viewModel.followedSubreddits.collectAsStateWithLifecycle()

    val currentFeed by viewModel.currentFeed.collectAsStateWithLifecycle()
    val currentSortOption by viewModel.currentSortOption.collectAsStateWithLifecycle()
    val currentSortTimeframe by viewModel.currentSortTimeframe.collectAsStateWithLifecycle()

    FeedScreen(
        postListUiState = homeFeedUiState,
        instanceUrl = instance,
        currentFeed = currentFeed,
        currentSortOption = currentSortOption,
        currentSortTimeframe = currentSortTimeframe,
        followedSubreddits = followedSubreddits,
        loadPosts = viewModel::loadMorePosts,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onImageClick = onImageClick,
        onUserClick = onUserClick,
        onSearchClick = onSearchClick,
        onVideoClick = onVideoClick,
        onSettingsClick = onSettingsClick,
        onAboutClick = onAboutClick,
        loadSortedPosts = viewModel::loadSortedPosts,
        changeFeed = viewModel::changeFeed,
        changeSort = viewModel::changeSort,
        changeTimeframe = viewModel::changeTimeframe,
        getPostLink = viewModel::getPostLink,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        navBar = navBar,
        modifier = modifier,
        shouldBlurNsfw = blurNsfw,
        shouldBlurSpoiler = blurSpoiler
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedScreen(
    postListUiState: PostListUiState,
    instanceUrl: String,
    currentFeed: Feed,
    currentSortOption: SortOption.Post,
    currentSortTimeframe: SortOption.Timeframe,
    followedSubreddits: List<Subreddit>,
    loadPosts: (SortOption.Post, SortOption.Timeframe, List<String>) -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onUserClick: (String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    loadSortedPosts: (SortOption.Post, SortOption.Timeframe, List<String>) -> Unit,
    changeFeed: (Feed) -> Unit,
    changeSort: (SortOption.Post) -> Unit,
    changeTimeframe: (SortOption.Timeframe) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    getPostLink: (Post) -> String,
    navBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shouldBlurNsfw: Boolean = false,
    shouldBlurSpoiler: Boolean = false,
    sortSheetState: SheetState = rememberModalBottomSheetState(),
    feedSheetState: SheetState = rememberModalBottomSheetState()
) {
    val isLoading = postListUiState is PostListUiState.Loading
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val shouldLoadMorePosts by remember {
        derivedStateOf {
            !listState.canScrollForward && listState.layoutInfo.totalItemsCount > 0
        }
    }

    val isTitleVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isMenuExpanded by remember { mutableStateOf(false) }

    var showSortSheet by remember { mutableStateOf(false) }

    var showFeedSheet by remember { mutableStateOf(false) }
    var shouldDisableFollowedFeed by rememberSaveable { mutableStateOf(false) }

    if (shouldLoadMorePosts) {
        loadPosts(
            currentSortOption,
            currentSortTimeframe,
            if (currentFeed == Feed.FOLLOWED)
                followedSubreddits.map { it.subredditName }
            else
                listOf(currentFeed.uri)
        )
    }

    Scaffold(
        topBar = {
            KiteTopAppBar(
                title =
                if (isTitleVisible)
                    ""
                else
                    stringResource(commonStrings.home_top_app_bar_title),
                searchIcon = KiteIcons.Search,
                searchIconContentDescription = stringResource(com.potatosheep.kite.core.common.R.string.content_desc_search),
                optionsIcon = KiteIcons.MoreOptions,
                optionsContentDescription = stringResource(com.potatosheep.kite.core.common.R.string.content_desc_more_options),
                onSearchClick = {
                    onSearchClick(
                        SortOption.Search.RELEVANCE,
                        SortOption.Timeframe.ALL,
                        if (currentFeed == Feed.FOLLOWED)
                            null
                        else
                            currentFeed.uri,
                        ""
                    )
                },
                onOptionsClick = { isMenuExpanded = true },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LocalBackgroundColor.current
                ),
                scrollBehavior = scrollBehavior
            )

            Box(Modifier.alignToRightOfParent()) {
                val menuOptions = MenuOption.entries

                KiteDropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                ) {
                    menuOptions.forEach { option ->
                        KiteDropdownMenuItem(
                            text = option.label,
                            onClick =
                            when (option) {
                                MenuOption.SORT -> {
                                    {
                                        showSortSheet = true
                                        isMenuExpanded = false
                                    }
                                }

                                MenuOption.SETTINGS -> {
                                    {
                                        onSettingsClick()
                                        isMenuExpanded = false
                                    }
                                }

                                MenuOption.ABOUT -> {
                                    {
                                        onAboutClick()
                                        isMenuExpanded = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        bottomBar = navBar,
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
                is PostListUiState.Error -> {
                    ErrorMsg(
                        msg = postListUiState.msg,
                        onRetry = {
                            loadSortedPosts(
                                currentSortOption,
                                currentSortTimeframe,
                                if (currentFeed == Feed.FOLLOWED)
                                    followedSubreddits.map { it.subredditName }
                                else
                                    listOf(currentFeed.uri)
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                is PostListUiState.Success -> {
                    LaunchedEffect(followedSubreddits) {
                        shouldDisableFollowedFeed = followedSubreddits.isEmpty()

                        if (shouldDisableFollowedFeed && currentFeed == Feed.FOLLOWED) {
                            changeFeed(Feed.POPULAR)
                        }
                    }

                    LazyColumn(
                        state = listState,
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
                                text = instanceUrl,
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
                                    currentFeed = currentFeed
                                )

                                PostSorter(
                                    onClick = { showSortSheet = true },
                                    currentSortOption = currentSortOption,
                                    currentSortTimeframe = currentSortTimeframe
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
                                blurImage = (shouldBlurNsfw && post.isNsfw) ||
                                            (shouldBlurSpoiler && post.isSpoiler),
                                isBookmarked = isBookmarked,
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

            if (isLoading) KiteLoadingIndicator(Modifier.fillMaxSize())

            val coroutineScope = rememberCoroutineScope()

            SortSheet(
                showBottomSheet = showSortSheet,
                sheetState = sortSheetState,
                onDismissRequest = { showSortSheet = false },
                onFilterClick = { sortOption, timeframe ->
                    changeSort(sortOption)
                    changeTimeframe(timeframe)

                    coroutineScope.launch {
                        listState.requestScrollToItem(0)
                        scrollBehavior.state.contentOffset = 0f
                    }

                    loadSortedPosts(
                        sortOption,
                        timeframe,
                        if (currentFeed == Feed.FOLLOWED)
                            followedSubreddits.map { it.subredditName }
                        else
                            listOf(currentFeed.uri)
                    )
                },
                currentSortOption = currentSortOption,
                currentSortTimeframe = currentSortTimeframe
            )

            FeedSheet(
                showBottomSheet = showFeedSheet,
                sheetState = feedSheetState,
                onDismissRequest = { showFeedSheet = false },
                onFeedClick = { feed ->
                    changeFeed(feed)

                    coroutineScope.launch {
                        listState.requestScrollToItem(0)
                        scrollBehavior.state.contentOffset = 0f
                    }

                    if (feed == Feed.FOLLOWED)
                        loadSortedPosts(
                            currentSortOption,
                            currentSortTimeframe,
                            followedSubreddits.map { it.subredditName }
                        )
                    else
                        loadSortedPosts(
                            currentSortOption,
                            currentSortTimeframe,
                            listOf(feed.uri)
                        )
                },
                currentFeed = currentFeed,
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
                instanceUrl = "https://test.com",
                currentFeed = Feed.POPULAR,
                currentSortOption = SortOption.Post.HOT,
                currentSortTimeframe = SortOption.Timeframe.DAY,
                followedSubreddits = emptyList(),
                loadPosts = { _, _, _ -> },
                onPostClick = { _, _, _, _ -> },
                onSubredditClick = {},
                onImageClick = { _, _ -> },
                onUserClick = {},
                onSearchClick = { _, _, _, _ -> },
                onVideoClick = {},
                onSettingsClick = {},
                onAboutClick = {},
                loadSortedPosts = { _, _, _ -> },
                changeFeed = {},
                changeSort = {},
                changeTimeframe = {},
                getPostLink = { _ -> "" },
                checkPostBookmarked = { _ -> false },
                bookmarkPost = {},
                removePostBookmark = {},
                shouldBlurNsfw = false,
                navBar = {}
            )
        }
    }
}