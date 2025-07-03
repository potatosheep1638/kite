package com.potatosheep.kite.feature.search

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.ErrorMsg
import com.potatosheep.kite.core.designsystem.KiteBottomSheet
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
import com.potatosheep.kite.core.common.R.string as commonStrings

@Composable
fun SearchRoute(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String, String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val timeframe by viewModel.timeframe.collectAsStateWithLifecycle()
    val subredditScope by viewModel.subredditScope.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val subredditListingUiState by viewModel.subredditListingUiState.collectAsStateWithLifecycle()

    val blurNsfw by viewModel.blurNsfw.collectAsStateWithLifecycle()
    val blurSpoiler by viewModel.blurSpoiler.collectAsStateWithLifecycle()

    SearchScreen(
        searchUiState = searchUiState,
        subredditListingUiState = subredditListingUiState,
        sortOption = sortOption,
        timeframe = timeframe,
        subredditScope = subredditScope,
        query = query,
        blurNsfw = blurNsfw,
        blurSpoiler = blurSpoiler,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onVideoClick = onVideoClick,
        onSearchClick = onSearchClick,
        changeSortOption =  viewModel::changeSortOption,
        changeSubredditScope = viewModel::changeSubredditScope,
        searchPostsAndSubreddits = viewModel::searchPostsAndSubreddits,
        loadMorePosts = viewModel::loadMorePosts,
        loadMoreSubreddits = viewModel::loadMoreSubreddits,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        checkIfValidUrl = viewModel::checkIfValidUrl,
        getPostLink = viewModel::getPostLink,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun SearchScreen(
    searchUiState: SearchUiState,
    subredditListingUiState: SubredditListingUiState,
    sortOption: SortOption.Search,
    timeframe: SortOption.Timeframe,
    subredditScope: String?,
    query: String,
    blurNsfw: Boolean,
    blurSpoiler: Boolean,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String, String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    searchPostsAndSubreddits: (String) -> Unit,
    changeSubredditScope: (String?) -> Unit,
    changeSortOption: (SortOption.Search, SortOption.Timeframe) -> Unit,
    loadMorePosts: (String, SortOption.Search, SortOption.Timeframe) -> Unit,
    loadMoreSubreddits: (String) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    checkIfValidUrl: (String) -> Boolean,
    getPostLink: (Post) -> String,
    modifier: Modifier = Modifier
) {
    val isLoading = searchUiState is SearchUiState.Loading
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val contentContainerColour =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainerLowest

    val sheetState = rememberModalBottomSheetState()
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSortOption by remember { mutableStateOf(sortOption) }
    var currentSortTimeframe by remember { mutableStateOf(timeframe) }
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val shouldLoadMorePosts by remember {
        derivedStateOf {
            !listState.canScrollForward && listState.layoutInfo.totalItemsCount > 1
        }
    }

    var isSearchBarFocused by remember { mutableStateOf(false) }
    var isSearchBarExpanded by remember { mutableStateOf(isSearchBarFocused) }
    var hasRequestedFocus by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var lastSubredditScope by remember { mutableStateOf("") }
    var newSubredditScope by remember { mutableStateOf(subredditScope) }

    // Variable that determines if the 'onSearch' lambda has been called.
    var isSearchClicked by remember { mutableStateOf(false) }

    if (shouldLoadMorePosts) {
        loadMorePosts(query, currentSortOption, currentSortTimeframe)
    }

    LaunchedEffect(Unit) {
        if (!hasRequestedFocus) {
            focusRequester.requestFocus()
            hasRequestedFocus = true
        }
    }

    LaunchedEffect(Unit) {
        if (query.isNotEmpty()) {
            isSearchBarExpanded = false
            focusManager.clearFocus()
        }
    }

    if (!isSearchBarFocused && !isSearchClicked) {
        if (lastSubredditScope.isNotEmpty())
            newSubredditScope = lastSubredditScope
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            KiteSearchBar(
                query = query,
                backIcon = KiteIcons.Back,
                onBackClick = onBackClick,
                onSearch = {
                    focusManager.clearFocus(true)
                    isSearchBarExpanded = isSearchBarFocused

                    if (checkIfValidUrl(it)) {
                        val pathSegments = it.split("/")

                        when (pathSegments.size) {
                            7, 8 -> {
                                onPostClick(
                                    pathSegments[4],
                                    pathSegments[6],
                                    null,
                                    null,
                                    it.contains("/s/")
                                )
                            }

                            9, 10 -> {
                                onPostClick(
                                    pathSegments[4],
                                    pathSegments[6],
                                    pathSegments[8],
                                    null,
                                    false
                                )
                            }

                            else -> Unit
                        }
                    } else {
                        if (searchUiState is SearchUiState.Initial) {
                            lastSubredditScope = ""
                            changeSubredditScope(newSubredditScope)
                            changeSortOption(currentSortOption, currentSortTimeframe)

                            searchPostsAndSubreddits(it)

                            coroutineScope.launch {
                                listState.requestScrollToItem(0)
                            }
                        } else {
                            isSearchClicked = true
                            onSearchClick(
                                currentSortOption,
                                currentSortTimeframe,
                                newSubredditScope,
                                it
                            )
                        }
                    }
                },
                expanded = isSearchBarExpanded,
                onExpandedChange = { isSearchBarExpanded = it },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { isSearchBarFocused = it.isFocused },
                leadingComposable = {
                    AnimatedVisibility(
                        visible = !newSubredditScope.isNullOrEmpty(),
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        InputChip(
                            selected = false,
                            onClick = {},
                            label = {
                                Text(
                                    text = newSubredditScope ?: lastSubredditScope,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            modifier = Modifier.padding(end = 12.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        focusRequester.requestFocus()

                                        if (!subredditScope.isNullOrEmpty()) {
                                            lastSubredditScope = subredditScope
                                        }

                                        newSubredditScope = null
                                    },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        imageVector = KiteIcons.Clear,
                                        contentDescription = "Remove scope",
                                    )
                                }
                            }
                        )
                    }
                },
                onClear = {
                    if (!isSearchBarFocused) {
                        focusRequester.requestFocus()
                        isSearchBarExpanded = true
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
                PostSorter(
                    onClick = { showBottomSheet = true },
                    currentSortOption = currentSortOption,
                    currentSortTimeframe = currentSortTimeframe,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
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

            when (searchUiState) {
                SearchUiState.Initial -> Unit
                SearchUiState.Loading -> Unit
                SearchUiState.EmptyQuery -> Unit
                is SearchUiState.Error -> {
                    ErrorMsg(
                        msg = searchUiState.msg,
                        onRetry = {
                            searchPostsAndSubreddits(query)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                is SearchUiState.Success -> {
                    KiteTabRow(
                        tabs = listOf(
                            stringResource(commonStrings.posts),
                            stringResource(commonStrings.subreddits)
                        ),
                        screens = listOf(
                            {
                                LazyColumn(
                                    state = listState
                                ) {
                                    itemsIndexed(
                                        items = searchUiState.posts,
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
                                                    newQuery
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
                                            blurImage = (blurNsfw && post.isNsfw) ||
                                                    (blurSpoiler && post.isSpoiler),
                                            colors = CardDefaults.cardColors(
                                                containerColor = contentContainerColour
                                            )
                                        )
                                    }
                                }
                            },
                            {
                                Column(Modifier.fillMaxSize()) {
                                    if (subredditListingUiState is SubredditListingUiState.Success
                                        || subredditListingUiState is SubredditListingUiState.Loading
                                    ) {
                                        SubredditSearchResultCard(
                                            subredditListingUiState = subredditListingUiState,
                                            subreddits = searchUiState.subreddits,
                                            onSubredditClick = onSubredditClick,
                                            addSubredditScope = { newSubredditScope = it },
                                            onSeeMoreClick = { expanded ->
                                                if (searchUiState.subreddits.size < 4) {
                                                    loadMoreSubreddits(query)
                                                } else if (!expanded) {
                                                    coroutineScope.launch {
                                                        listState.requestScrollToItem(0)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            showAddScopeButton = subredditScope.isNullOrEmpty(),
                                            onSubredditScopeChanged = { focusRequester.requestFocus() }
                                        )
                                    } else if (subredditListingUiState is SubredditListingUiState.NoResult) {
                                        NoResultsMsg(
                                            title = stringResource(commonStrings.no_result),
                                            subtitle = stringResource(commonStrings.no_result_subreddit_subtitle),
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

            if (isLoading) KiteLoadingIndicator(Modifier.fillMaxSize())

            if ((searchUiState is SearchUiState.Success &&
                        searchUiState.posts.isEmpty() &&
                        searchUiState.subreddits.isEmpty()) ||
                searchUiState is SearchUiState.EmptyQuery
            ) {
                NoResultsMsg(
                    title = stringResource(commonStrings.no_result),
                    subtitle = stringResource(commonStrings.no_result_subtitle),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        KiteBottomSheet(
            showBottomSheet = showBottomSheet,
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
                keyboard?.show()
            },
        ) {
            Text(
                text = "Sort options",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FlowRow(Modifier.padding(bottom = 12.dp)) {

                SortOption.Search.entries.forEach { option ->
                    val selected = option == currentSortOption

                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (currentSortOption != option) {
                                currentSortOption = option
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

                if (currentSortOption != SortOption.Search.NEW) {
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
                                if (currentSortTimeframe != timeframe) {
                                    currentSortTimeframe = timeframe
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
                                text = "${stringResource(commonStrings.see)} ${
                                    if (expanded)
                                        stringResource(commonStrings.less)
                                    else
                                        stringResource(commonStrings.more)
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
                                    stringResource(commonStrings.collapse_list_subreddit)
                                else
                                    stringResource(commonStrings.expand_list_subreddit),
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
private fun PostSorter(
    onClick: () -> Unit,
    currentSortOption: SortOption.Search,
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
                        if (currentSortOption != SortOption.Search.NEW)
                            " •  ${stringResource(currentSortTimeframe.label)}"
                        else
                            ""
                    }",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            selected = true,
            leadingIcon = {
                Icon(
                    imageVector = KiteIcons.Check,
                    contentDescription = stringResource(currentSortOption.label),
                    modifier = Modifier.size(16.dp)
                )
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
private fun SearchScreenPreview(
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
            SearchScreen(
                searchUiState = SearchUiState.Success(
                    posts = posts,
                    subreddits = subreddits
                ),
                subredditListingUiState = SubredditListingUiState.Success,
                sortOption = SortOption.Search.RELEVANCE,
                timeframe = SortOption.Timeframe.ALL,
                subredditScope = "r/testsub",
                query = "",
                blurNsfw = false,
                blurSpoiler = false,
                onBackClick = {},
                onPostClick = { _, _, _, _, _ -> },
                onSubredditClick = {},
                onUserClick = {},
                onImageClick = { _, _ -> },
                onVideoClick = { _, _ -> },
                onSearchClick = { _, _, _, _ -> },
                searchPostsAndSubreddits = {},
                changeSubredditScope = {},
                changeSortOption = { _, _ -> },
                loadMorePosts = { _, _, _ -> },
                loadMoreSubreddits = {},
                checkPostBookmarked = { _ -> false },
                bookmarkPost = {},
                removePostBookmark = {},
                checkIfValidUrl = { _ -> false },
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