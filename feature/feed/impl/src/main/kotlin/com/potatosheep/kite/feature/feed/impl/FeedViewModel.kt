package com.potatosheep.kite.feature.feed.impl

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.SubredditRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.translation.R.string as Translation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
    private val _postListUiState = MutableStateFlow<PostListUiState>(PostListUiState.Loading)
    val postListUiState: StateFlow<PostListUiState> = _postListUiState

    private val _shouldRefresh = MutableStateFlow(RefreshScope.NO_REFRESH)
    val shouldRefresh: StateFlow<RefreshScope> = _shouldRefresh

    private val _feedOptions = MutableStateFlow(
        FeedSettings(
            feed = Feed.FOLLOWED,
            sort = SortOption.Post.HOT,
            timeframe = SortOption.Timeframe.DAY
        )
    )

    private val _previousInstance = savedStateHandle.getStateFlow(PREV_INSTANCE, "")
    private val _previousSubreddits = savedStateHandle.getStateFlow(PREV_SUBREDDITS, emptyList<String>())
    private val _previousShowNsfw = savedStateHandle.getStateFlow(PREV_SHOW_NSFW, false)

    private val _lazyListState: StateFlow<LazyListState> = MutableStateFlow(LazyListState(0, 0))

    private val _shouldScrollToTop = MutableStateFlow(false)
    val shouldScrollToTop = _shouldScrollToTop

    val feedUiState = combine(
        subredditRepository.getFollowedSubreddits(),
        userConfigRepository.userConfig,
        _feedOptions,
        _lazyListState,
    ) { subreddits, config, options, listState ->
        FeedUiState.Success(
            instanceUrl = config.instance,
            followedSubreddits = subreddits.map { it.subredditName },
            showNsfw = config.showNsfw,
            blurNsfw = config.blurNsfw,
            blurSpoiler = config.blurSpoiler,
            currentFeed = options.feed,
            sort = options.sort,
            timeframe = options.timeframe,
            listState = listState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = FeedUiState.Loading
    ).apply {
        viewModelScope.launch {
            this@apply.collectLatest { state ->
                if (state is FeedUiState.Success) {
                    val isInstanceSame = state.instanceUrl == _previousInstance.value

                    val containsPreviousSubscriptions =
                        state.followedSubreddits.containsAll(_previousSubreddits.value) &&
                                state.followedSubreddits.size == _previousSubreddits.value.size

                    val isShowNsfwSame = state.showNsfw == _previousShowNsfw.value

                    when {
                        isInstanceSame && containsPreviousSubscriptions && isShowNsfwSame -> {
                            _shouldRefresh.value = RefreshScope.NO_REFRESH
                        }

                        !isShowNsfwSame -> {
                            _shouldRefresh.value = RefreshScope.GLOBAL
                        }

                        isInstanceSame && !containsPreviousSubscriptions -> {
                            _shouldRefresh.value = RefreshScope.FOLLOWED_ONLY
                        }

                        else -> {
                            _shouldRefresh.value = RefreshScope.GLOBAL
                        }
                    }

                    Log.d("FeedViewModel", "${_shouldRefresh.value}")
                }
            }
        }
    }

    @Synchronized
    fun loadSortedPosts(
        sort: SortOption.Post,
        timeframe: SortOption.Timeframe,
        subredditScopes: List<String> = emptyList(),
        loadMore: Boolean = false
    ) {
        viewModelScope.launch {
            if (feedUiState.value is FeedUiState.Success) {
                var posts: MutableList<Post> = mutableListOf()
                var after: String? = null

                runCatching {
                    if (loadMore && _postListUiState.value is PostListUiState.Success) {
                        posts =
                            (_postListUiState.value as PostListUiState.Success).posts.toMutableList()
                        after = posts.last().id
                    } else {
                        _postListUiState.value = PostListUiState.Loading
                    }

                    postRepository.getPosts(
                        instanceUrl = (feedUiState.value as FeedUiState.Success).instanceUrl,
                        sort = sort.uri,
                        timeframe = timeframe.uri,
                        after = after,
                        subredditName =
                            if (subredditScopes.isEmpty() || subredditScopes[0] == FOLLOWED_FEED)
                                null
                            else
                                subredditScopes.joinToString(separator = "+")
                    )
                }.onSuccess {
                    if (loadMore) {
                        posts.addAll(it)
                        _postListUiState.value = PostListUiState.Success(posts)
                    } else {
                        _postListUiState.value = PostListUiState.Success(it)
                        _shouldScrollToTop.value = true
                    }
                }.onFailure {
                    _postListUiState.value = PostListUiState.Error(it.message.toString())
                }
            }
        }
    }

    @Synchronized
    fun loadFrontPage() {
        viewModelScope.launch {
            if (feedUiState.value is FeedUiState.Success) {
                _postListUiState.value = PostListUiState.Loading

                val feedUiState = (feedUiState.value as FeedUiState.Success)

                runCatching {
                    val redirect = if (feedUiState.currentFeed != Feed.FOLLOWED) {
                        "r/${feedUiState.currentFeed.uri}"
                    } else {
                        ""
                    }

                    updateUiState(
                        sort = SortOption.Post.HOT,
                        timeframe = SortOption.Timeframe.DAY
                    )

                    userConfigRepository.getInstanceCookies(
                        instanceUrl = feedUiState.instanceUrl,
                        redirect = redirect,
                        sort = SortOption.Post.HOT.uri,
                        subreddits = feedUiState.followedSubreddits,
                        showNsfw = feedUiState.showNsfw
                    )
                }.onSuccess {
                    _shouldRefresh.value = RefreshScope.NO_REFRESH
                    _postListUiState.value = PostListUiState.Success(it)
                    _shouldScrollToTop.value = true
                }.onFailure {
                    _postListUiState.value = PostListUiState.Error(it.message.toString())
                }

                savedStateHandle[PREV_INSTANCE] = feedUiState.instanceUrl
                savedStateHandle[PREV_SUBREDDITS] = feedUiState.followedSubreddits
                savedStateHandle[PREV_SHOW_NSFW] = feedUiState.showNsfw
            }
        }
    }

    fun updateUiState(
        feed: Feed? = null,
        sort: SortOption.Post? = null,
        timeframe: SortOption.Timeframe? = null
    ) {
        viewModelScope.launch {
            _feedOptions.value = FeedSettings(
                feed = feed ?: _feedOptions.value.feed,
                sort = sort ?: _feedOptions.value.sort,
                timeframe = timeframe ?: _feedOptions.value.timeframe
            )
        }
    }

    fun getPostLink(post: Post) =
        "${(feedUiState.value as FeedUiState.Success).instanceUrl}/r/${post.subredditName}/comments/${post.id}"

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

    fun scrolledToTop() {
        viewModelScope.launch {
            _shouldScrollToTop.value = false
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

// UI state for feed screen settings
sealed interface FeedUiState {

    // TODO: Implement this
    // data object LoadingMore : FeedUiState

    data object Loading : FeedUiState

    data class Success(
        val instanceUrl: String,
        val followedSubreddits: List<String>,
        val showNsfw: Boolean,
        val blurNsfw: Boolean,
        val blurSpoiler: Boolean,
        val currentFeed: Feed,
        val sort: SortOption.Post,
        val timeframe: SortOption.Timeframe,
        val listState: LazyListState
    ) : FeedUiState
}

private data class FeedSettings(
    val feed: Feed,
    val sort: SortOption.Post,
    val timeframe: SortOption.Timeframe
)

enum class Feed(
    val label: Int,
    val uri: String
) {
    FOLLOWED(
        label = Translation.home_feed_followed,
        uri = FOLLOWED_FEED
    ),
    POPULAR(
        label = Translation.home_feed_popular,
        uri = "popular"
    ),
    ALL(
        label = Translation.home_feed_all,
        uri = "all"
    )
}

enum class RefreshScope {
    FOLLOWED_ONLY,
    GLOBAL,
    NO_REFRESH
}


const val PREV_INSTANCE = "previousInstance"
const val PREV_SUBREDDITS = "previousSubreddits"
const val PREV_SHOW_NSFW = "previousShowNsfw"
const val FOLLOWED_FEED = "followedFeed"
