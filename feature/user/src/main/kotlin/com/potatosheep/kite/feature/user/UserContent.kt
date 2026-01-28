package com.potatosheep.kite.feature.user

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
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.KiteBottomSheet
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteLoadingIndicator
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.R
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.User
import com.potatosheep.kite.core.ui.UserCommentCard
import com.potatosheep.kite.core.ui.param.PostsAndComments
import com.potatosheep.kite.core.ui.param.PostsAndCommentsPreviewParameterProvider
import com.potatosheep.kite.core.ui.post.PostCard
import com.potatosheep.kite.core.translation.R.string as Translation

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalLayoutApi::class
)
@Composable
internal fun UserContent(
    user: User,
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
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?) -> Unit,
    onAboutClick: () -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val contentContainerColour =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainerLowest

    val isTitleVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    val shouldLoadMorePosts by remember {
        derivedStateOf {
            !listState.canScrollForward && listState.layoutInfo.totalItemsCount > 3
        }
    }

    var selectedOption by rememberSaveable { mutableStateOf(SortOption.User.HOT) }

    if (shouldLoadMorePosts) {
        loadMorePostsAndComments(selectedOption)
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
                        user.userName
                    } else {
                        ""
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LocalBackgroundColor.current
                    ),
                    scrollBehavior = scrollBehavior,
                )
            },
            containerColor = LocalBackgroundColor.current,
            modifier = if (scrollBehavior != null) {
                modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                modifier
            },
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
                    state = listState
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                start = 12.dp,
                                top = 12.dp,
                                bottom = 24.dp,
                                end = 12.dp
                            )
                        ) {
                            Box {
                                if (user.iconLink.isEmpty()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.image),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(60.dp)
                                            .clickable {
                                                onAboutClick()
                                            }
                                    )
                                } else {
                                    AsyncImage(
                                        model = user.iconLink,
                                        placeholder = painterResource(id = R.drawable.image),
                                        contentDescription = null,
                                        modifier = modifier
                                            .then(sharedElementModifier)
                                            .clip(CircleShape)
                                            .size(60.dp)
                                            .clickable {
                                                onAboutClick()
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Column(Modifier.padding(horizontal = 12.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = KiteIcons.Karma,
                                        contentDescription = stringResource(Translation.content_desc_karma),
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Text(
                                        text = user.karma.toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }

                                Text(
                                    text = user.userName,
                                    style = MaterialTheme.typography.headlineLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    when (userFeedUiState) {
                        UserFeedUiState.Loading -> {
                            item {
                                KiteLoadingIndicator(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 12.dp)
                                )
                            }
                        }

                        is UserFeedUiState.Success -> {
                            itemsIndexed(
                                items = userFeedUiState.postAndComments,
                                /**
                                 * Index is appended to the key in case Redlib returns the same post
                                 * twice, which results in an [IllegalArgumentException] exception being
                                 * thrown.
                                 */
                                key = { index, item ->
                                    "$index/${item.hashCode()}"
                                }
                            ) { _, item ->
                                when (item) {
                                    is Post -> {
                                        val onClickFunction = {
                                            onPostClick(
                                                item.subredditName,
                                                item.id,
                                                null,
                                                when {
                                                    item.mediaLinks.isEmpty() -> null

                                                    item.mediaLinks[0].mediaType == MediaType.GALLERY_THUMBNAIL ||
                                                            item.mediaLinks[0].mediaType == MediaType.ARTICLE_THUMBNAIL ||
                                                            item.mediaLinks[0].mediaType == MediaType.VIDEO_THUMBNAIL -> {

                                                        item.mediaLinks[0].link
                                                    }

                                                    else -> null
                                                }
                                            )
                                        }

                                        var isBookmarked by remember { mutableStateOf(false) }
                                        var isChecking by remember { mutableStateOf(true) }

                                        LaunchedEffect(Unit) {
                                            isBookmarked = checkPostBookmarked(item)
                                            isChecking = false
                                        }

                                        PostCard(
                                            post = item,
                                            onClick = onClickFunction,
                                            onLongClick = {
                                                clipboardManager.setText(
                                                    AnnotatedString(
                                                        item.title
                                                    )
                                                )
                                            },
                                            onSubredditClick = onSubredditClick,
                                            onUserClick = {},
                                            onFlairClick = onFlairClick,
                                            onImageClick = onImageClick,
                                            onVideoClick = onVideoClick,
                                            onShareClick = { onShare(getPostLink(item), context) },
                                            onBookmarkClick = {
                                                if (isBookmarked && !isChecking) {
                                                    removePostBookmark(item)
                                                } else if (!isChecking) {
                                                    bookmarkPost(item)
                                                }

                                                isBookmarked = !isBookmarked
                                            },
                                            onSubredditLongClick = {
                                                onSearchClick(
                                                    SortOption.Search.RELEVANCE,
                                                    SortOption.Timeframe.ALL,
                                                    it
                                                )
                                            },
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            ),
                                            showText = false,
                                            blurImage = (shouldBlurNsfw && item.isNsfw) ||
                                                    (shouldBlurSpoiler && item.isSpoiler),
                                            isBookmarked = isBookmarked,
                                            colors = CardDefaults.cardColors(
                                                containerColor = contentContainerColour
                                            )
                                        )
                                    }

                                    is Comment -> {
                                        UserCommentCard(
                                            comment = item,
                                            onClick = {
                                                onPostClick(
                                                    item.subredditName,
                                                    item.postId,
                                                    item.id,
                                                    null
                                                )
                                            },
                                            onSubredditClick = onSubredditClick,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp,
                                            ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = contentContainerColour
                                            )
                                        )
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }
                }
            }

            KiteBottomSheet(
                showBottomSheet = showBottomSheet,
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                modifier = modifier
            ) {
                val sortOptions = SortOption.User.entries

                Text(
                    text = "Sort options",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FlowRow(Modifier.padding(bottom = 12.dp)) {
                    sortOptions.forEach { option ->
                        val selected = option == selectedOption

                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (selectedOption != option) {
                                    selectedOption = option

                                    loadSortedPostsAndComments(option)
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
@PreviewLightDark
private fun UserPreview(
    @PreviewParameter(PostsAndCommentsPreviewParameterProvider::class)
    postAndComments: PostsAndComments
) {
    val user = User(
        "TestName",
        "TestDesc",
        123,
        "www.testlink.com/img/image1.jpg"
    )

    val postsAndComments = listOf(
        postAndComments.posts[0],
        postAndComments.comments[3]
    )

    KiteTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            UserContent(
                user = user,
                userFeedUiState = UserFeedUiState.Success(postsAndComments),
                shouldBlurNsfw = false,
                shouldBlurSpoiler = false,
                loadSortedPostsAndComments = {},
                loadMorePostsAndComments = {},
                getPostLink = { "" },
                onBackClick = {},
                onPostClick = { _, _, _, _ -> },
                onSubredditClick = {},
                onImageClick = { _, _ -> },
                onSearchClick = { _, _, _ -> },
                onAboutClick = {},
                onFlairClick = { _, _, _, _ -> },
                onVideoClick = {},
                checkPostBookmarked = { _ -> false },
                bookmarkPost = {},
                removePostBookmark = {},
            )
        }
    }
}