package com.potatosheep.kite.feature.subreddit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.SubredditRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
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
class SubredditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userConfigRepository: UserConfigRepository,
    private val subredditRepository: SubredditRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val subreddit =
        savedStateHandle.toRoute<com.potatosheep.kite.feature.subreddit.nav.Subreddit>().subreddit

    private val _postUiState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val postUiState: StateFlow<PostUiState> = _postUiState

    private val _subredditUiState = MutableStateFlow<SubredditUiState>(SubredditUiState.Loading)
    val subredditUiState: StateFlow<SubredditUiState> = _subredditUiState

    val isSubredditFollowed = subredditRepository.checkIfSubredditHasRecord(subreddit)
        .map { it > 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = false
        )

    val blurNsfw = userConfigRepository.userConfig
        .map { it.blurNsfw }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = true
        )

    val blurSpoiler = userConfigRepository.userConfig
        .map { it.blurSpoiler }
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

    init {
        loadSubreddit()
    }

    fun loadSubreddit() {
        viewModelScope.launch {
            runCatching {
                _subredditUiState.value = SubredditUiState.Loading

                subredditRepository.getSubreddit(
                    instanceUrl = instanceUrl.first { it.isNotEmpty() },
                    subredditName = subreddit
                )
            }.onSuccess {
                _subredditUiState.value = SubredditUiState.Success(
                    subreddit = it.first,
                )

                _postUiState.value = PostUiState.Success(
                    posts = it.second,
                    sort = SortOption.Post.HOT
                )
            }.onFailure {
                _subredditUiState.value = SubredditUiState.Error(
                    msg = it.message.toString()
                )
            }
        }
    }

    fun loadSortedPosts(
        sort: SortOption.Post,
        timeframe: SortOption.Timeframe
    ) {
        viewModelScope.launch {
            _postUiState.value = PostUiState.Loading

            runCatching {
                postRepository.getPosts(
                    instanceUrl.value,
                    sort = sort.uri,
                    timeframe = timeframe.uri,
                    subredditName = subreddit
                )
            }.onSuccess {
                _postUiState.value = PostUiState.Success(
                    posts = it,
                    sort = sort
                )
            }.onFailure {

            }
        }
    }

    @Synchronized
    fun loadMorePosts(
        sort: SortOption.Post,
        timeframe: SortOption.Timeframe
    ) {
        viewModelScope.launch {
            if (_postUiState.value is PostUiState.Success) {

                val posts = (_postUiState.value as PostUiState.Success).posts.toMutableList()

                runCatching {
                    val after = posts.last().id

                    postRepository.getPosts(
                        instanceUrl = instanceUrl.value,
                        sort = sort.uri,
                        timeframe = timeframe.uri,
                        subredditName = subreddit,
                        after = after
                    )
                }.onSuccess {
                    posts.addAll(it)

                    _postUiState.value = PostUiState.Success(
                        posts = posts,
                        sort = sort
                    )
                }.onFailure {

                }
            }
        }
    }

    fun followSubreddit(subreddit: Subreddit, follow: Boolean) {
        viewModelScope.launch {
            subredditRepository.setSubredditFollowed(subreddit, follow)
        }
    }

    suspend fun checkIfPostExists(post: Post): Boolean =
        postRepository.checkIfPostHasRecord(post.id) > 0

    fun bookmarkPost(post: Post) {
        viewModelScope.launch {
            postRepository.savePost(post)
        }
    }

    fun removePostBookmark(post: Post) {
        viewModelScope.launch {
            postRepository.removeSavedPost(post)
        }
    }

    fun getPostLink(post: Post) = "${instanceUrl.value}/r/${post.subredditName}/comments/${post.id}"
}

sealed interface SubredditUiState {
    data object Loading : SubredditUiState

    data class Error(
        val msg: String
    ) : SubredditUiState

    data class Success(
        val subreddit: Subreddit,
    ) : SubredditUiState
}

sealed interface PostUiState {
    data object Loading : PostUiState

    data class Success(
        val posts: List<Post>,
        val sort: SortOption.Post
    ) : PostUiState
}