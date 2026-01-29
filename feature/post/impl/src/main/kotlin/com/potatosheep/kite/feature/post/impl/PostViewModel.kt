package com.potatosheep.kite.feature.post.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.potatosheep.kite.core.common.Metadata
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.MediaLink
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.feature.post.impl.nav.PostRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userConfigRepository: UserConfigRepository,
    private val postRepository: PostRepository,
): ViewModel() {

    private val postRoute = savedStateHandle.toRoute<PostRoute>()

    private val _uiState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val uiState: StateFlow<PostUiState> = _uiState

    private val instanceUrl = userConfigRepository.userConfig
        .map {
            if (it.shouldUseCustomInstance)
                it.customInstance
            else
                it.instance
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = ""
        )

    init {
        loadPost()
    }

    fun loadPost() {
        viewModelScope.launch {
            runCatching {
                _uiState.value = PostUiState.Loading

                postRepository.getPost(
                    instanceUrl = instanceUrl.first { it.isNotBlank() },
                    subredditName = postRoute.subreddit,
                    postId = postRoute.postId,
                    replyId = postRoute.commentId,
                    isShareLink = postRoute.isShareLink,
                    findParentComments = postRoute.findParents
                )
            }.onSuccess {
                val commentVisibilityStateMap = it.second.associate { comment ->
                    comment.id to true
                }

                val post = if (it.first.mediaLinks.isNotEmpty() &&
                    it.first.mediaLinks[0].mediaType == MediaType.VIDEO_THUMBNAIL) {

                    it.first.copy(
                        mediaLinks = listOf(
                            MediaLink(
                                link = postRoute.thumbnailLink ?: "",
                                caption = null,
                                mediaType = MediaType.VIDEO_THUMBNAIL
                            ),
                            it.first.mediaLinks[1]
                        )
                    )
                } else {
                    it.first
                }

                val comments = it.second

                val isFullDiscussion = it.third
                    .split("/")
                    .size < 8

                _uiState.value = PostUiState.Success(
                    post = post,
                    comments = comments,
                    indents = getIndents(comments, post.id),
                    commentVisibilityStateMap = commentVisibilityStateMap,
                    isFullDiscussion = isFullDiscussion,
                    hasParentComments = !isFullDiscussion && comments[0].parentCommentId == Metadata.HAS_PARENTS
                )
            }.onFailure {
                _uiState.value = PostUiState.Error(
                    it.message.toString()
                )
            }
        }
    }

    suspend fun checkIfPostExists(post: Post): Boolean =
        postRepository.checkIfPostHasRecord(post.id) > 0

    fun bookmarkPost(post: Post) {
        viewModelScope.launch {
            val mediaType = when {
                post.mediaLinks.isEmpty() -> {
                    null
                }

                post.mediaLinks[0].mediaType == MediaType.GALLERY_IMAGE -> {
                    MediaType.GALLERY_THUMBNAIL
                }

                post.mediaLinks[0].mediaType == MediaType.ARTICLE_THUMBNAIL -> {
                    MediaType.ARTICLE_THUMBNAIL
                }

                post.mediaLinks[0].mediaType == MediaType.VIDEO_THUMBNAIL -> {
                    MediaType.VIDEO_THUMBNAIL
                }

                else -> null
            }

            val postWithThumbnail =
                if (mediaType != null) {
                    post.copy(
                        mediaLinks = listOf(
                            MediaLink(
                                mediaType = mediaType,
                                link = this@PostViewModel.postRoute.thumbnailLink ?: "",
                                caption = null
                            ),
                            MediaLink(
                                mediaType = when (mediaType) {
                                    MediaType.GALLERY_THUMBNAIL -> {
                                        MediaType.GALLERY_LINK
                                    }

                                    MediaType.ARTICLE_THUMBNAIL -> {
                                        MediaType.ARTICLE_LINK
                                    }

                                    MediaType.VIDEO_THUMBNAIL -> {
                                        MediaType.VIDEO
                                    }

                                    else -> MediaType.GALLERY_LINK
                                },
                                link = post.mediaLinks[1].link,
                                caption = null
                            )
                        )
                    )
                } else {
                    post
                }

            postRepository.savePost(postWithThumbnail)
        }
    }

    fun removePostBookmark(post: Post) {
        viewModelScope.launch {
            postRepository.removeSavedPost(post)
        }
    }

    @Synchronized
    fun changeCommentVisibilityState(
        startingComment: Comment,
        collapse: Boolean
    ) {
        viewModelScope.launch {
            val uiStateSuccess = (_uiState.value as PostUiState.Success)

            val stateMap = uiStateSuccess.commentVisibilityStateMap.toMutableMap()
            val comments = uiStateSuccess.comments

            if (collapse) {
                stateMap[startingComment.id] = false

                comments.forEach { comment ->
                    if (stateMap[comment.parentCommentId] == false) {
                        stateMap[comment.id] = false
                    }
                }
            } else {
                var start = false
                stateMap[startingComment.id] = true

                for (comment in comments) {
                    if (comment.parentCommentId == startingComment.parentCommentId && start) {
                        break
                    } else if (stateMap[comment.parentCommentId] == true && start) {
                        stateMap[comment.id] = true
                    } else if (comment.id == startingComment.id) {
                        start = true
                    }
                }
            }

            _uiState.value = PostUiState.Success(
                post = uiStateSuccess.post,
                comments = comments,
                indents = getIndents(comments, uiStateSuccess.post.id),
                commentVisibilityStateMap = stateMap,
                isFullDiscussion = uiStateSuccess.isFullDiscussion,
                hasParentComments = uiStateSuccess.hasParentComments
            )
        }
    }

    fun getPostLink(post: Post) = "${instanceUrl.value}/r/${post.subredditName}/comments/${post.id}"
}

/**
 * Function that determines the indents for each [Comment].
 *
 * This function assumes that the [Comment] objects provided to it are stored in sequence, and will
 * not work correctly if they are not. For example, given the following comment structure:
 *
 *      | Comment A
 *      || Comment B
 *      ||| Comment C
 *      ||| Comment D
 *      | Comment E
 *
 * The [Comment] objects must be stored in this order:
 *
 *      A, B, C, D, E
 *
 * When the above list (hereinafter referred to as List A) is supplied to the function, it will
 * return a list of integers (List B):
 *
 *      0, 1, 2, 2, 0
 *
 * Each number in List B denotes the indent amount for the comment with a matching index in List A.
 * Here's both lists written as key/value pairs:
 *
 *      {A, 0}, {B, 1}, {C, 2}, {D, 2}, {E, 0}
 */
// TODO: Change return type to Map<Comment, Int>
private fun getIndents(comments: List<Comment>, postId: String): List<Int> {
    val stack = ArrayDeque<String>(comments.count())
    val commentIndents = mutableListOf<Int>()
    var indentCount = 0

    comments.forEach { comment ->
        if (comment.parentCommentId == postId || comment.parentCommentId == Metadata.HAS_PARENTS) {
            if (stack.isNotEmpty()) {
                stack.clear()
                indentCount = 0
            }

            stack.addFirst(comment.id)
            commentIndents.add(indentCount)
        }
        else if (stack.isNotEmpty() && comment.parentCommentId == stack.first()) {
            stack.addFirst(comment.id)
            indentCount++

            commentIndents.add(indentCount)
        }
        else {
            while (stack.isNotEmpty()) {
                if (comment.parentCommentId == stack.first()) {
                    stack.addFirst(comment.id)
                    indentCount++

                    commentIndents.add(indentCount)
                    break
                } else {
                    stack.removeFirst()
                    indentCount--
                }
            }
        }
    }

    return commentIndents
}

// TODO: Add more states
sealed interface PostUiState {
    data object Loading : PostUiState

    data class Error(
        val msg: String
    ) : PostUiState

    data class Success(
        val post: Post,
        val comments: List<Comment>,
        val indents: List<Int>,
        val commentVisibilityStateMap: Map<String, Boolean>,
        val isFullDiscussion: Boolean,
        val hasParentComments: Boolean
    ): PostUiState
}