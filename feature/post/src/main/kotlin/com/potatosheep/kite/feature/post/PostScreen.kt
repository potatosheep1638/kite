package com.potatosheep.kite.feature.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.Metadata
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.onShare
import com.potatosheep.kite.core.designsystem.ErrorMsg
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.markdown.util.toRedditMarkdown
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.CommentCard
import com.potatosheep.kite.core.ui.MoreRepliesCard
import com.potatosheep.kite.core.ui.param.PostsAndComments
import com.potatosheep.kite.core.ui.param.PostsAndCommentsPreviewParameterProvider
import com.potatosheep.kite.core.ui.post.PostCard
import kotlinx.coroutines.launch
import com.potatosheep.kite.core.common.R.string as commonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRoute(
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    // TODO: rename this
    onMoreRepliesClick: (String, String, String?, String?, Boolean, Boolean) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = hiltViewModel()
) {
    val postUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val postScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )

    PostScreen(
        postUiState = postUiState,
        getPostLink = viewModel::getPostLink,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onMoreRepliesClick = onMoreRepliesClick,
        onFlairClick = onFlairClick,
        onVideoClick = onVideoClick,
        onBackClick = onBackClick,
        loadPost = viewModel::loadPost,
        checkPostBookmarked = viewModel::checkIfPostExists,
        bookmarkPost = viewModel::bookmarkPost,
        removePostBookmark = viewModel::removePostBookmark,
        scrollBehavior = postScrollBehavior,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostScreen(
    postUiState: PostUiState,
    getPostLink: (Post) -> String,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onMoreRepliesClick: (String, String, String?, String?, Boolean, Boolean) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    loadPost: () -> Unit,
    checkPostBookmarked: suspend (Post) -> Boolean,
    bookmarkPost: (Post) -> Unit,
    removePostBookmark: (Post) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val isLoading = postUiState is PostUiState.Loading
    val contentContainerColour =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainerLowest

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            SmallTopAppBar(
                backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                backIconContentDescription = stringResource(commonStrings.back),
                onBackClick = onBackClick,
                title = stringResource(com.potatosheep.kite.core.common.R.string.post),
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = contentContainerColour
                ),
            )
        },
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
            when (postUiState) {
                PostUiState.Loading -> Unit
                is PostUiState.Error -> {
                    ErrorMsg(
                        msg = postUiState.msg,
                        onRetry = {
                            loadPost()
                        },
                        modifier = Modifier
                            .background(contentContainerColour)
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                is PostUiState.Success -> {
                    var commentVisibilityStateMap by rememberSaveable {
                        mutableStateOf(postUiState.commentVisibilityStateMap)
                    }

                    val localDensity = LocalDensity.current
                    val configuration = LocalConfiguration.current

                    LazyColumn(
                        state = listState,
                    ) {
                        item {
                            val post = postUiState.post
                            var postHeight by rememberSaveable { mutableIntStateOf(0) }

                            var isBookmarked by remember { mutableStateOf(false) }
                            var isChecking by remember { mutableStateOf(true) }

                            LaunchedEffect(Unit) {
                                isBookmarked = checkPostBookmarked(post)
                                isChecking = false
                            }

                            var orientation by rememberSaveable { mutableIntStateOf(configuration.orientation) }

                            LaunchedEffect(configuration.orientation) {
                                snapshotFlow { configuration.orientation }
                                    .collect {
                                        if (orientation != configuration.orientation)
                                            postHeight = 0

                                        orientation = it
                                    }
                            }

                            Box {
                                Spacer(
                                    Modifier.height(
                                        with(localDensity) {
                                            postHeight.toDp()
                                        }
                                    )
                                )

                                PostCard(
                                    post = post,
                                    onClick = {},
                                    onLongClick = {
                                        clipboardManager.setText(
                                            AnnotatedString(
                                                if (post.textContent.isEmpty())
                                                    post.title
                                                else
                                                    post.textContent.toRedditMarkdown()
                                            )
                                        )
                                    },
                                    onSubredditClick = onSubredditClick,
                                    onUserClick = onUserClick,
                                    onFlairClick = onFlairClick,
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
                                    modifier = Modifier.onSizeChanged {
                                        if (postHeight < it.height)
                                            postHeight = it.height
                                    },
                                    showText = true,
                                    blurImage = false,
                                    galleryRedirect = false,
                                    isBookmarked = isBookmarked,
                                    shape = RectangleShape,
                                    colors = CardDefaults.cardColors(
                                        containerColor = contentContainerColour
                                    )
                                )
                            }
                        }

                        item {
                            Box(Modifier.background(contentContainerColour)) {
                                Row(
                                    modifier = Modifier
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    AnimatedVisibility(
                                        visible = !postUiState.isFullDiscussion,
                                        enter = scaleIn(),
                                        exit = scaleOut()
                                    ) {
                                        AssistChip(
                                            onClick = {
                                                onMoreRepliesClick(
                                                    postUiState.post.subredditName,
                                                    postUiState.post.id,
                                                    null,
                                                    when (postUiState.post.mediaLinks[0].mediaType) {
                                                        MediaType.VIDEO_THUMBNAIL -> {
                                                            postUiState.post.mediaLinks[0].link
                                                        }

                                                        else -> null
                                                    },
                                                    false,
                                                    false
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = stringResource(commonStrings.post_view_all_comments),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            },
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = postUiState.hasParentComments,
                                        enter = scaleIn(),
                                        exit = scaleOut()
                                    ) {
                                        AssistChip(
                                            onClick = {
                                                onMoreRepliesClick(
                                                    postUiState.post.subredditName,
                                                    postUiState.post.id,
                                                    postUiState.comments[0].id,
                                                    when (postUiState.post.mediaLinks[0].mediaType) {
                                                        MediaType.VIDEO_THUMBNAIL -> {
                                                            postUiState.post.mediaLinks[0].link
                                                        }

                                                        else -> null
                                                    },
                                                    false,
                                                    true
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = stringResource(commonStrings.post_view_parent_comment),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            },
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        itemsIndexed(
                            items = postUiState.comments,
                            key = { _, comment -> "${comment.userName}/${comment.id}" }
                        ) { index, comment ->
                            val showComment =
                                commentVisibilityStateMap[comment.parentCommentId] == true ||
                                        comment.parentCommentId == postUiState.post.id ||
                                        comment.parentCommentId == Metadata.HAS_PARENTS

                            if (index == 0 || comment.userName != "") {

                                var showExpandIcon by rememberSaveable {
                                    mutableStateOf(
                                        showComment && commentVisibilityStateMap[comment.id] == false
                                    )
                                }

                                DisposableEffect(commentVisibilityStateMap) {
                                    showExpandIcon =
                                        showComment && commentVisibilityStateMap[comment.id] == false
                                    onDispose {}
                                }

                                var commentHeight by rememberSaveable { mutableIntStateOf(0) }
                                var orientation by rememberSaveable {
                                    mutableIntStateOf(
                                        configuration.orientation
                                    )
                                }

                                LaunchedEffect(configuration.orientation) {
                                    snapshotFlow { configuration.orientation }
                                        .collect {
                                            if (orientation != configuration.orientation)
                                                commentHeight = 0

                                            orientation = it
                                        }
                                }

                                AnimatedVisibility(
                                    visible = showComment,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Box {
                                        Spacer(
                                            Modifier.height(
                                                with(localDensity) { commentHeight.toDp() }
                                            )
                                        )

                                        CommentCard(
                                            comment = comment,
                                            indents = postUiState.indents[index],
                                            onClick = {
                                                showExpandIcon = !showExpandIcon

                                                coroutineScope.launch {
                                                    commentVisibilityStateMap =
                                                        commentCollapser(
                                                            startingComment = comment,
                                                            comments = postUiState.comments,
                                                            stateMap = commentVisibilityStateMap,
                                                            collapse = showExpandIcon
                                                        )
                                                }
                                            },
                                            onLongClick = {
                                                clipboardManager.setText(
                                                    AnnotatedString(
                                                        comment.textContent.toRedditMarkdown()
                                                    )
                                                )
                                            },
                                            onUserClick = onUserClick,
                                            modifier = Modifier
                                                .padding(
                                                    top = if (postUiState.indents[index] == 0)
                                                        12.dp
                                                    else
                                                        0.dp
                                                )
                                                .onSizeChanged {
                                                    if (commentHeight < it.height || showExpandIcon)
                                                        commentHeight = it.height
                                                },
                                            isTopLevelComment = comment.parentCommentId == postUiState.post.id ||
                                                    comment.parentCommentId == Metadata.HAS_PARENTS,
                                            shape = RectangleShape,
                                            expanded = !showExpandIcon,
                                            colors = CardDefaults.cardColors(
                                                containerColor = contentContainerColour
                                            )
                                        )
                                    }

                                    Spacer(
                                        Modifier
                                            .height(12.dp)
                                            .background(MaterialTheme.colorScheme.surfaceContainer)
                                    )
                                }
                            } else {
                                AnimatedVisibility(
                                    visible = showComment,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    MoreRepliesCard(
                                        comment = comment,
                                        onClick = onMoreRepliesClick,
                                        indents = postUiState.indents[index],
                                        modifier = Modifier.fillMaxWidth(),
                                        thumbnailLink = when {
                                            postUiState.post.mediaLinks.isEmpty() -> null

                                            postUiState.post.mediaLinks[0].mediaType == MediaType.VIDEO_THUMBNAIL -> {
                                                postUiState.post.mediaLinks[0].link
                                            }

                                            else -> null
                                        },
                                        shape = RectangleShape,
                                        colors = CardDefaults.cardColors(
                                            containerColor = contentContainerColour
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(contentContainerColour)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(64.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PostScreenPreview(
    @PreviewParameter(PostsAndCommentsPreviewParameterProvider::class) postAndComments: PostsAndComments
) {
    val posts = postAndComments.posts
    val comments = postAndComments.comments

    KiteTheme {
        PostScreen(
            postUiState = PostUiState.Success(
                posts[3],
                comments,
                listOf(0, 1, 1, 2),
                mapOf(
                    comments[0].id to true,
                    comments[1].id to true,
                    comments[2].id to true,
                    comments[3].id to true,
                ),
                false,
                hasParentComments = true
            ),
            getPostLink = { "" },
            onSubredditClick = {},
            onUserClick = {},
            onImageClick = { _, _ -> },
            onMoreRepliesClick = { _, _, _, _, _, _ -> },
            onFlairClick = { _, _, _, _ -> },
            onVideoClick = {},
            onBackClick = {},
            loadPost = {},
            checkPostBookmarked = { _ -> false },
            bookmarkPost = {},
            removePostBookmark = {},
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        )
    }
}

// TODO: Consider moving this function to ViewModel and launch it in the Default coroutine scope.
private fun commentCollapser(
    startingComment: Comment,
    comments: List<Comment>,
    stateMap: Map<String, Boolean>,
    collapse: Boolean = true
): Map<String, Boolean> {
    val newStateMap = stateMap.toMutableMap()

    if (collapse) {
        var start = false
        newStateMap[startingComment.id] = false

        for (comment in comments) {
            if (start && comment.parentCommentId == startingComment.parentCommentId) {
                break
            } else if (start && newStateMap[comment.parentCommentId] == false) {
                newStateMap[comment.id] = false
            } else if (comment.id == startingComment.id) {
                start = true
            }
        }
    } else {
        var start = false
        newStateMap[startingComment.id] = true

        for (comment in comments) {
            if (start && comment.parentCommentId == startingComment.parentCommentId) {
                break
            } else if (start && newStateMap[comment.parentCommentId] == true) {
                newStateMap[comment.id] = true
            } else if (comment.id == startingComment.id) {
                start = true
            }
        }
    }

    return newStateMap
}