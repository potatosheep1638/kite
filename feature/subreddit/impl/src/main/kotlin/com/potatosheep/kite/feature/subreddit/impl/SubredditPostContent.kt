package com.potatosheep.kite.feature.subreddit.impl

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.abbreviate
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.KiteBottomSheet
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteLoadingIndicator
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.R
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider
import com.potatosheep.kite.core.ui.post.PostCard
import kotlinx.coroutines.launch
import com.potatosheep.kite.core.translation.R.string as Translation

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SubredditPostContent(
    postUiState: PostUiState,
    subreddit: Subreddit,
    shouldBlurNsfw: Boolean,
    shouldBlurSpoiler: Boolean,
    isSubredditFollowed: Boolean,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onAboutClick: () -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onUserClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    loadSortedPosts: (SortOption.Post, SortOption.Timeframe) -> Unit,
    loadMorePosts: (SortOption.Post, SortOption.Timeframe) -> Unit,
    setSubredditFollowed: (Subreddit, Boolean) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    getPostLink: (Post) -> String,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val contentContainerColour =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainerLowest

    val isTitleVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    val shouldLoadMorePosts by remember {
        derivedStateOf {
            !lazyListState.canScrollForward && lazyListState.layoutInfo.totalItemsCount > 3
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSortOption by rememberSaveable { mutableStateOf(SortOption.Post.HOT) }
    var currentSortTimeframe by rememberSaveable { mutableStateOf(SortOption.Timeframe.DAY) }

    val coroutineScope = rememberCoroutineScope()

    if (shouldLoadMorePosts) {
        loadMorePosts(currentSortOption, currentSortTimeframe)
    }

    with(sharedTransitionScope) {
        val sharedElementModifier =
            if (this != null && animatedVisibilityScope != null) {
                Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "about"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            } else {
                Modifier
            }

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    backIcon = KiteIcons.Back,
                    onBackClick = onBackClick,
                    actions = {
                        IconButton(
                            onClick = {
                                onSearchClick(
                                    SortOption.Search.RELEVANCE,
                                    SortOption.Timeframe.ALL,
                                    subreddit.subredditName,
                                    ""
                                )
                            }
                        ) {
                            Icon(
                                imageVector = KiteIcons.Search,
                                contentDescription = stringResource(Translation.content_desc_search)
                            )
                        }

                        IconButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Right
                                )
                            )
                        ) {
                            Icon(
                                imageVector = KiteIcons.Sort,
                                contentDescription = stringResource(Translation.content_desc_sort)
                            )
                        }
                    },
                    title = if (!isTitleVisible) {
                        subreddit.subredditName
                    } else {
                        ""
                    },
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
                LazyColumn(
                    state = lazyListState,
                ) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(
                                    start = 12.dp,
                                    top = 12.dp,
                                    end = 12.dp
                                )
                                .fillMaxWidth()
                        ) {
                            Box {
                                if (subreddit.iconLink.isNotBlank()) {
                                    AsyncImage(
                                        model = subreddit.iconLink,
                                        placeholder = painterResource(id = R.drawable.image),
                                        contentDescription = null,
                                        modifier = modifier
                                            .then(sharedElementModifier)
                                            .clip(CircleShape)
                                            .size(80.dp)
                                            .clickable {
                                                onAboutClick()
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.image),
                                        contentDescription = null,
                                        modifier = modifier
                                            .then(sharedElementModifier)
                                            .clip(CircleShape)
                                            .size(80.dp)
                                            .clickable {
                                                onAboutClick()
                                            }
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = subreddit.subredditName,
                                    style = MaterialTheme.typography.headlineLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                Row(
                                    modifier = Modifier.padding(top = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = KiteIcons.Subscribers,
                                        contentDescription = stringResource(Translation.subscribers),
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Text(
                                        text = "${subreddit.subscribers.abbreviate()} ${
                                            stringResource(
                                                Translation.subreddit_subscribers
                                            )
                                        }",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(start = 4.dp),
                                    )

                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    Icon(
                                        imageVector = KiteIcons.ActiveUsers,
                                        contentDescription = stringResource(Translation.active_users),
                                        modifier = Modifier.size(17.dp)
                                    )

                                    Text(
                                        text = "${subreddit.activeUsers.abbreviate()} ${
                                            stringResource(
                                                Translation.subreddit_online
                                            )
                                        }",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(start = 5.dp),
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                )
                                .fillMaxWidth()
                        ) {
                            FilterChip(
                                onClick = { showBottomSheet = true },
                                label = {
                                    Text(
                                        text = "${stringResource(currentSortOption.label)}${
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
                                        contentDescription = stringResource(Translation.content_desc_sort)
                                    )
                                },
                                modifier = Modifier.padding(end = 6.dp)
                            )


                            Row(Modifier.alignToRightOfParent(0.dp, 0.dp)) {
                                Button(
                                    onClick = {
                                        setSubredditFollowed(subreddit, !isSubredditFollowed)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor =
                                            if (isSubredditFollowed)
                                                LocalBackgroundColor.current
                                            else
                                                MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = if (isSubredditFollowed)
                                            stringResource(Translation.unfollow)
                                        else
                                            stringResource(Translation.follow),
                                        color =
                                            if (isSubredditFollowed)
                                                MaterialTheme.colorScheme.error
                                            else
                                                MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }

                    when (postUiState) {
                        PostUiState.Loading -> {
                            item {
                                KiteLoadingIndicator(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 12.dp)
                                )
                            }
                        }

                        is PostUiState.Success -> {
                            currentSortOption = postUiState.sort

                            itemsIndexed(
                                items = postUiState.posts,
                                /**
                                 * Index is appended to the key in case Redlib returns the same post
                                 * twice, which results in an [IllegalArgumentException] exception being
                                 * thrown.
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
                                    onSubredditClick = {},
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
                                    isBookmarked = isBookmarked,
                                    blurImage = (shouldBlurNsfw && post.isNsfw) ||
                                            (shouldBlurSpoiler && post.isSpoiler),
                                    colors = CardDefaults.cardColors(
                                        containerColor = contentContainerColour
                                    )
                                )
                            }
                        }
                    }
                }
            }

            KiteBottomSheet(
                showBottomSheet = showBottomSheet,
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
            ) {
                Text(
                    text = "Sort options",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FlowRow(Modifier.padding(bottom = 12.dp)) {

                    SortOption.Post.entries.forEach { option ->
                        val selected = option == currentSortOption

                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (currentSortOption != option) {
                                    currentSortOption = option

                                    coroutineScope.launch {
                                        if (scrollBehavior != null) {
                                            scrollBehavior.state.contentOffset = 0f
                                        }
                                    }

                                    loadSortedPosts(currentSortOption, currentSortTimeframe)
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
                                    if (currentSortTimeframe != timeframe) {
                                        currentSortTimeframe = timeframe

                                        coroutineScope.launch {
                                            if (scrollBehavior != null) {
                                                scrollBehavior.state.contentOffset = 0f
                                            }
                                        }

                                        loadSortedPosts(currentSortOption, currentSortTimeframe)
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun SubredditScreenPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    val subreddit = Subreddit(
        subredditName = "placeholder",
        subscribers = 3834987,
        activeUsers = 22394,
        iconLink = "https://redlib.example.com/style/blahblah.jpg",
        description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
        sidebar = "RULE 1: blah blah blah",
    )

    KiteTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
            SubredditPostContent(
                postUiState = PostUiState.Success(posts, SortOption.Post.HOT),
                subreddit = subreddit,
                shouldBlurNsfw = false,
                shouldBlurSpoiler = false,
                isSubredditFollowed = false,
                onBackClick = {},
                onPostClick = { _, _, _, _ -> },
                onImageClick = { _, _ -> },
                onAboutClick = {},
                onSearchClick = { _, _, _, _ -> },
                onUserClick = {},
                onVideoClick = {},
                loadSortedPosts = { _, _ -> },
                loadMorePosts = { _, _ -> },
                setSubredditFollowed = { _, _ -> },
                checkPostBookmarked = { _ -> false },
                bookmarkPost = {},
                removePostBookmark = {},
                getPostLink = { "" },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            )
        }
    }
}