package com.potatosheep.kite.feature.searchresult.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.ErrorMsg
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteLoadingIndicator
import com.potatosheep.kite.core.designsystem.KiteSearchBar
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.NoResultsMsg
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.ui.SubredditRow
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider
import com.potatosheep.kite.core.ui.post.PostCard
import kotlinx.coroutines.launch
import com.potatosheep.kite.core.translation.R.string as Translation

@Composable
fun SearchResultRoute(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = hiltViewModel()
) {
    val searchUiState by viewModel.postListUiState.collectAsStateWithLifecycle()
    val subredditListingUiState by viewModel.subredditListingUiState.collectAsStateWithLifecycle()
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle()

    SearchResultScreen(
        postListUiState = searchUiState,
        subredditListingUiState = subredditListingUiState,
        searchResultUiState = searchResultUiState,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onVideoClick = onVideoClick,
        onSearchClick = onSearchClick,
        searchPostsAndSubreddits = viewModel::searchPostsAndSubreddits,
        loadMorePosts = viewModel::loadMorePosts,
        loadMoreSubreddits = viewModel::loadMoreSubreddits,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        getPostLink = viewModel::getPostLink,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchResultScreen(
    postListUiState: PostListUiState,
    subredditListingUiState: SubredditListingUiState,
    searchResultUiState: SearchResultUiState,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String, Boolean) -> Unit,
    searchPostsAndSubreddits: (String) -> Unit,
    loadMorePosts: (String, SortOption.Search, SortOption.Timeframe) -> Unit,
    loadMoreSubreddits: (String) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    getPostLink: (Post) -> String,
    modifier: Modifier = Modifier
) {
    val isLoading = postListUiState is PostListUiState.Loading
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val contentContainerColour =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainerLowest

    val focusRequester = remember { FocusRequester() }

    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val shouldLoadMorePosts by remember {
        derivedStateOf {
            !listState.canScrollForward && listState.layoutInfo.totalItemsCount > 1
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(Modifier.clickable {
                if (searchResultUiState is SearchResultUiState.Success) {
                    onSearchClick(
                        searchResultUiState.sortOption,
                        searchResultUiState.timeframe,
                        searchResultUiState.subredditScope,
                        searchResultUiState.query,
                        (postListUiState is PostListUiState.Success &&
                                postListUiState.posts.isEmpty() &&
                                postListUiState.subreddits.isEmpty()) ||
                                postListUiState is PostListUiState.EmptyQuery
                    )
                }
            }) {
                KiteSearchBar(
                    query = if (searchResultUiState is SearchResultUiState.Success)
                        searchResultUiState.query else "",
                    backIcon = KiteIcons.Back,
                    onBackClick = onBackClick,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    enabled = false,
                    leadingComposable = {
                        val subredditScope = if (searchResultUiState is SearchResultUiState.Success)
                            searchResultUiState.subredditScope ?: "" else ""
                        AnimatedVisibility(
                            visible = subredditScope.isNotEmpty(),
                            enter = scaleIn(),
                            exit = scaleOut()
                        ) {
                            InputChip(
                                selected = false,
                                onClick = {},
                                label = {
                                    Text(
                                        text = subredditScope,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                modifier = Modifier.padding(end = 12.dp),
                            )
                        }
                    },
                    inputFieldColors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    colors = SearchBarDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
                ) {}
            }
        },
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
            /*HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                thickness = Dp.Hairline,
                color = MaterialTheme.colorScheme.onSurface
            )*/
            when (searchResultUiState) {
                SearchResultUiState.Loading -> Unit
                is SearchResultUiState.Success -> {

                    when (postListUiState) {
                        PostListUiState.Loading -> Unit
                        PostListUiState.EmptyQuery -> Unit
                        is PostListUiState.Error -> {
                            ErrorMsg(
                                msg = postListUiState.msg,
                                onRetry = {
                                    searchPostsAndSubreddits(searchResultUiState.query)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp)
                            )
                        }

                        is PostListUiState.Success -> {
                            KiteTabRow(
                                tabs = listOf(
                                    stringResource(Translation.posts),
                                    stringResource(Translation.subreddits)
                                ),
                                screens = listOf(
                                    {
                                        LazyColumn(
                                            state = listState
                                        ) {
                                            itemsIndexed(
                                                items = postListUiState.posts,
                                                /**
                                                 * Index is appended to the key in case Redlib returns the same post
                                                 * twice, which results in an [IllegalArgumentException] exception being
                                                 * thrown.
                                                 */
                                                key = { index, post -> "${index}/${post.subredditName}/${post.id}" }
                                            ) { _, post ->
                                                val onPostClickFun = {
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
                                                        },
                                                        false
                                                    )
                                                }

                                                val onFlairClickFun: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit =
                                                    { _, _, _, newQuery ->
                                                        onSearchClick(
                                                            SortOption.Search.RELEVANCE,
                                                            SortOption.Timeframe.ALL,
                                                            post.subredditName,
                                                            newQuery,
                                                            false
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
                                                    onClick = onPostClickFun,
                                                    onLongClick = {
                                                        clipboardManager.setText(
                                                            AnnotatedString(
                                                                post.title
                                                            )
                                                        )
                                                    },
                                                    onSubredditClick = onSubredditClick,
                                                    onUserClick = onUserClick,
                                                    onFlairClick = onFlairClickFun,
                                                    onImageClick = onImageClick,
                                                    onVideoClick = onVideoClick,
                                                    onShareClick = {
                                                        onShare(
                                                            getPostLink(post),
                                                            context
                                                        )
                                                    },
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
                                                    isBookmarked = isBookmarked,
                                                    onSubredditLongClick = {
                                                        onSearchClick(
                                                            searchResultUiState.sortOption,
                                                            searchResultUiState.timeframe,
                                                            post.subredditName,
                                                            searchResultUiState.query,
                                                            false
                                                        )
                                                    },
                                                    blurImage = (searchResultUiState.blurNsfw && post.isNsfw) ||
                                                            (searchResultUiState.blurSpoiler && post.isSpoiler),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = contentContainerColour
                                                    )
                                                )
                                            }
                                        }

                                        if (shouldLoadMorePosts) {
                                            loadMorePosts(
                                                searchResultUiState.query,
                                                searchResultUiState.sortOption,
                                                searchResultUiState.timeframe
                                            )
                                        }
                                    },
                                    {
                                        Column(Modifier.fillMaxSize()) {
                                            if (subredditListingUiState is SubredditListingUiState.Success
                                                || subredditListingUiState is SubredditListingUiState.Loading
                                            ) {
                                                SubredditSearchResultCard(
                                                    subredditListingUiState = subredditListingUiState,
                                                    subreddits = postListUiState.subreddits,
                                                    onSubredditClick = onSubredditClick,
                                                    addSubredditScope = {
                                                        onSearchClick(
                                                            searchResultUiState.sortOption,
                                                            searchResultUiState.timeframe,
                                                            it,
                                                            searchResultUiState.query,
                                                            false
                                                        )
                                                    },
                                                    onSeeMoreClick = { expanded ->
                                                        if (postListUiState.subreddits.size < 4) {
                                                            loadMoreSubreddits(searchResultUiState.query)
                                                        } else if (!expanded) {
                                                            coroutineScope.launch {
                                                                listState.requestScrollToItem(0)
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    showAddScopeButton = true,
                                                    onSubredditScopeChanged = { focusRequester.requestFocus() }
                                                )
                                            } else if (subredditListingUiState is SubredditListingUiState.NoResult) {
                                                NoResultsMsg(
                                                    title = stringResource(Translation.no_result),
                                                    subtitle = stringResource(Translation.no_result_subreddit_subtitle),
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                ),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (isLoading) KiteLoadingIndicator(Modifier.fillMaxSize())

            if ((postListUiState is PostListUiState.Success &&
                        postListUiState.posts.isEmpty() &&
                        postListUiState.subreddits.isEmpty()) ||
                postListUiState is PostListUiState.EmptyQuery
            ) {
                NoResultsMsg(
                    title = stringResource(Translation.no_result),
                    subtitle = stringResource(Translation.no_result_subtitle),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// TODO: Optimise for lazy loading (i.e., split this into mutliple composables)
@Composable
private fun SubredditSearchResultCard(
    subredditListingUiState: SubredditListingUiState,
    subreddits: List<Subreddit>,
    onSubredditClick: (String) -> Unit,
    addSubredditScope: (String) -> Unit,
    onSeeMoreClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showAddScopeButton: Boolean = false,
    onSubredditScopeChanged: () -> Unit = {},
    colors: CardColors = CardDefaults.cardColors(
        containerColor = LocalBackgroundColor.current
    )
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = colors
    ) {
        if (subredditListingUiState is SubredditListingUiState.Loading) KiteLoadingIndicator(
            Modifier.fillMaxSize()
        )

        LazyColumn {
            when (subredditListingUiState) {
                SubredditListingUiState.NoResult -> Unit
                SubredditListingUiState.Loading -> Unit
                SubredditListingUiState.Success -> {
                    itemsIndexed(
                        items = subreddits,
                        key = { _, subreddit -> subreddit.subredditName }
                    ) { i, subreddit ->
                        AnimatedVisibility(
                            visible = i < 3 || expanded,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            SubredditRow(
                                subreddit = subreddit,
                                onClick = { onSubredditClick(subreddit.subredditName) },
                                iconButtonIcon = KiteIcons.Add,
                                modifier = Modifier.padding(
                                    horizontal = 24.dp,
                                    vertical = 14.dp
                                ),
                                showIconButton = showAddScopeButton,
                                onIconButtonClick = {
                                    addSubredditScope(subreddit.subredditName)
                                    onSubredditScopeChanged()
                                }
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .padding(
                                    top = 16.dp,
                                    bottom = 12.dp
                                )
                                .fillMaxWidth()
                                .clickable {
                                    expanded = !expanded
                                    onSeeMoreClick(expanded)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${stringResource(Translation.see)} ${
                                    if (expanded)
                                        stringResource(Translation.less)
                                    else
                                        stringResource(Translation.more)
                                }",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )

                            Icon(
                                imageVector =
                                if (expanded)
                                    KiteIcons.Collapse
                                else
                                    KiteIcons.DropdownAlt,
                                contentDescription =
                                if (expanded)
                                    stringResource(Translation.collapse_list_subreddit)
                                else
                                    stringResource(Translation.expand_list_subreddit),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KiteTabRow(
    tabs: List<String>,
    screens: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabs.size }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        indicator = { tabPos ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    tabPos[pagerState.currentPage],
                    pagerState
                )
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontFamily = KiteFonts.InterMedium,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { index ->
        screens.getOrNull(index)?.invoke()
    }
}

private fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    pagerState: PagerState
): Modifier =
    composed(
        inspectorInfo =
        debugInspectorInfo {
            name = "tabIndicatorOffset"
            value = currentTabPosition
        }
    ) {
        val currentTabWidth by animateDpAsState(
            targetValue = currentTabPosition.width,
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            ),
            label = ""
        )
        val indicatorOffset by animateDpAsState(
            targetValue = calculateIndicatorOffset(currentTabPosition, pagerState),
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            ),
            label = ""
        )
        fillMaxWidth()
            .wrapContentSize(Alignment.BottomStart)
            .offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
            .width(currentTabWidth)
    }

@Composable
private fun calculateIndicatorOffset(
    currentTabPosition: TabPosition,
    pagerState: PagerState
): Dp {
    val isDragging = pagerState.interactionSource.collectIsDraggedAsState()

    if (!isDragging.value) {
        return currentTabPosition.left
    }

    if (pagerState.isScrollInProgress) {
        if (pagerState.settledPage == pagerState.currentPage) {
            return currentTabPosition.left + (currentTabPosition.width * pagerState.currentPageOffsetFraction)
        }

        /*val offsetFraction = (1 - abs(pagerState.currentPageOffsetFraction))
        val settledPageLeft = currentTabPosition.left - currentTabPosition.width

        return settledPageLeft + (currentTabPosition.width * offsetFraction)*/
    }

    return currentTabPosition.left
}

@PreviewLightDark
@Composable
private fun SearchResultScreenPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    val subreddits = listOf(
        Subreddit(
            subredditName = "r/placeholder",
            subscribers = 3834987,
            activeUsers = 22394,
            iconLink = "https://redlib.example.com/style/blahblah.jpg",
            description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
            sidebar = "RULE 1: blah blah blah",
        ),
        Subreddit(
            subredditName = "r/placeholder",
            subscribers = 3834987,
            activeUsers = 22394,
            iconLink = "https://redlib.example.com/style/blahblah.jpg",
            description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
            sidebar = "RULE 1: blah blah blah",
        )
    )

    KiteTheme {
        Surface {
            SearchResultScreen(
                postListUiState = PostListUiState.Success(
                    posts = posts,
                    subreddits = subreddits
                ),
                subredditListingUiState = SubredditListingUiState.Success,
                searchResultUiState = SearchResultUiState.Success(
                    sortOption = SortOption.Search.RELEVANCE,
                    timeframe = SortOption.Timeframe.ALL,
                    subredditScope = "r/testsub",
                    query = "test",
                    instance = "",
                    blurNsfw = false,
                    blurSpoiler = false,
                ),
                onBackClick = {},
                onPostClick = { _, _, _, _, _ -> },
                onSubredditClick = {},
                onUserClick = {},
                onImageClick = { _, _ -> },
                onVideoClick = {},
                onSearchClick = { _, _, _, _, _ -> },
                searchPostsAndSubreddits = {},
                loadMorePosts = { _, _, _ -> },
                loadMoreSubreddits = {},
                checkPostBookmarked = { _ -> false },
                bookmarkPost = {},
                removePostBookmark = {},
                getPostLink = { "" },
            )
        }
    }
}

@Preview
@Composable
private fun SubredditSearchCardPreview() {
    val subreddits = listOf(
        Subreddit(
            subredditName = "r/placeholder",
            subscribers = 3834987,
            activeUsers = 22394,
            iconLink = "https://redlib.example.com/style/blahblah.jpg",
            description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
            sidebar = "RULE 1: blah blah blah",
        ),
        Subreddit(
            subredditName = "r/placeholder1",
            subscribers = 3834987,
            activeUsers = 22394,
            iconLink = "https://redlib.example.com/style/blahblah.jpg",
            description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
            sidebar = "RULE 1: blah blah blah",
        )
    )

    KiteTheme {
        Surface {
            SubredditSearchResultCard(
                subredditListingUiState = SubredditListingUiState.Success,
                subreddits = subreddits,
                onSubredditClick = {},
                addSubredditScope = {},
                onSeeMoreClick = {}
            )
        }
    }
}