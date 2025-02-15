package com.potatosheep.kite.feature.bookmark

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    userConfigRepository: UserConfigRepository,
    private val postRepository: PostRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val query = savedStateHandle.getStateFlow(QUERY, "")

    val blurNsfw = userConfigRepository.userConfig
        .map { it.blurNsfw }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = true
        )

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

    private val _uiState = MutableStateFlow<PostListUiState>(PostListUiState.Loading)
    val uiState = _uiState

    init {
        viewModelScope.launch {
            query.collectLatest {
                val postFlow = postRepository.getSavedPosts(it)

                postFlow.map { postList ->
                    postList.map { post ->
                        post.copy(
                            mediaLinks = post.mediaLinks.map { mediaLink ->
                                when (mediaLink.mediaType) {
                                    MediaType.ARTICLE_THUMBNAIL, MediaType.ARTICLE_LINK -> {
                                        mediaLink
                                    }

                                    else -> {
                                        if (mediaLink.link.isEmpty()) {
                                            mediaLink
                                        } else {
                                            mediaLink.copy(
                                                link = "${instanceUrl.first { it.isNotEmpty() }}${mediaLink.link}"
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }.collectLatest {
                    _uiState.value = PostListUiState.Success(it)
                }
            }
        }
    }

    fun removeBookmarkedPost(post: Post) {
        viewModelScope.launch {
            postRepository.removeSavedPost(post)
        }
    }

    fun getPostLink(post: Post) = "${instanceUrl.value}/r/${post.subredditName}/comments/${post.id}"

    fun searchSavedPosts(query: String) {
        viewModelScope.launch {
            savedStateHandle[QUERY] = query
        }
    }
}

sealed interface PostListUiState {

    data object Loading : PostListUiState

    data class Success(
        val posts: List<Post>
    ) : PostListUiState
}

const val QUERY = "query"