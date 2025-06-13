package com.potatosheep.kite.feature.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.potatosheep.kite.core.common.R
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.SubredditRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class FeedViewModel @Inject constructor(
    subredditRepository: SubredditRepository,
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PostListUiState>(PostListUiState.Loading)
    val uiState: StateFlow<PostListUiState> = _uiState

    val currentFeed = savedStateHandle.getStateFlow(FEED, Feed.FOLLOWED)
    val currentSortOption = savedStateHandle.getStateFlow(SORT, SortOption.Post.HOT)
    val currentSortTimeframe = savedStateHandle.getStateFlow(TIME, SortOption.Timeframe.DAY)

    private val _shouldRefresh = MutableStateFlow(false)
    val shouldRefresh: StateFlow<Boolean> = _shouldRefresh

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

    val instanceUrl = userConfigRepository.userConfig
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

    val followedSubreddits = subredditRepository.getFollowedSubreddits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            instanceUrl.combine(followedSubreddits) { instance, subreddits ->
                Pair(instance, subreddits)
            }.collectLatest {
                if (it.first.isNotEmpty() && it.second != null) {
                    _shouldRefresh.value = true
                }
            }
        }
    }

    @Synchronized
    fun loadMorePosts(
        sort: SortOption.Post,
        timeframe: SortOption.Timeframe,
        subredditScopes: List<String>
    ) {
        viewModelScope.launch {
            if (_uiState.value is PostListUiState.Success) {
                val posts = (_uiState.value as PostListUiState.Success).posts.toMutableList()
                val after = posts.last().id

                runCatching {
                    postRepository.getPosts(
                        instanceUrl = instanceUrl.value,
                        sort = sort.uri,
                        timeframe = timeframe.uri,
                        after = after,
                        subredditName =
                            if (subredditScopes.isEmpty() || subredditScopes[0] == FOLLOWED_SUBREDDITS)
                                null
                            else
                                subredditScopes.joinToString(separator = "+")
                    )
                }.onSuccess {
                    posts.addAll(it)
                    _uiState.value = PostListUiState.Success(posts)
                }.onFailure {
                    // TODO: Do something
                }
            }
        }
    }

    @Synchronized
    fun loadSortedPosts(
        sort: SortOption.Post,
        timeframe: SortOption.Timeframe,
        subredditScopes: List<String> = emptyList(),
    ) {
        viewModelScope.launch {
            _uiState.value = PostListUiState.Loading

            runCatching {
                postRepository.getPosts(
                    instanceUrl = instanceUrl.value,
                    sort = sort.uri,
                    timeframe = timeframe.uri,
                    subredditName =
                        if (subredditScopes.isEmpty() || subredditScopes[0] == FOLLOWED_SUBREDDITS)
                            null
                        else
                            subredditScopes.joinToString(separator = "+")
                )
            }.onSuccess {
                _uiState.value = PostListUiState.Success(it)
            }.onFailure {
                _uiState.value = PostListUiState.Error(it.message.toString())
            }
        }
    }

    @Synchronized
    fun loadFrontPage() {
        viewModelScope.launch {
            _uiState.value = PostListUiState.Loading

            runCatching {
                changeSort(SortOption.Post.HOT)
                changeTimeframe(SortOption.Timeframe.DAY)

                userConfigRepository.getInstanceCookies(
                    instanceUrl = instanceUrl.value,
                    sort = SortOption.Post.HOT.uri,
                    subreddits = followedSubreddits.value!!.map { it.subredditName }
                )
            }.onSuccess {
                _shouldRefresh.value = false
                _uiState.value = PostListUiState.Success(it)
            }.onFailure {
                _uiState.value = PostListUiState.Error(it.message.toString())
            }
        }
    }

    fun changeFeed(feed: Feed) {
        savedStateHandle[FEED] = feed
    }

    fun changeSort(sort: SortOption.Post) {
        savedStateHandle[SORT] = sort
    }

    fun changeTimeframe(timeframe: SortOption.Timeframe) {
        savedStateHandle[TIME] = timeframe
    }

    fun getPostLink(post: Post) = "${instanceUrl.value}/r/${post.subredditName}/comments/${post.id}"

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
}

// UI state for post content
sealed interface PostListUiState {

    data object Loading : PostListUiState

    data class Error(
        val msg: String,
    ) : PostListUiState

    data class Success(
        val posts: List<Post>,
    ) : PostListUiState
}

enum class Feed(
    val label: Int,
    val uri: String
) {
    FOLLOWED(
        label = R.string.home_feed_followed,
        uri = FOLLOWED_SUBREDDITS
    ),
    POPULAR(
        label = R.string.home_feed_popular,
        uri = "popular"
    ),
    ALL(
        label = R.string.home_feed_all,
        uri = "all"
    )
}

// TODO: Implement this
// UI state for HomeScreen
sealed interface HomeUiState {

    data object LoadingMore : HomeUiState

    data object Loaded : HomeUiState

    data class LoadingError(
        val msg: String
    ) : HomeUiState
}

const val FEED = "feed"
const val SORT = "sort"
const val TIME = "time"
const val FOLLOWED_SUBREDDITS = "followedSubreddits"