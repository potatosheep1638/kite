package com.potatosheep.kite.feature.searchresult.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.SubredditRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = SearchResultViewModel.Factory::class)
class SearchResultViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val subredditRepository: SubredditRepository,
    userConfigRepository: UserConfigRepository,
    @Assisted("subredditScope") subredditScope: String?,
    @Assisted("sort") sort: SortOption.Search,
    @Assisted("timeframe") timeframe: SortOption.Timeframe,
    @Assisted("query") query: String
) : ViewModel() {

    private val _postListUiState = MutableStateFlow<PostListUiState>(PostListUiState.Loading)
    val postListUiState: StateFlow<PostListUiState> = _postListUiState

    private val _subredditListingUiState =
        MutableStateFlow<SubredditListingUiState>(SubredditListingUiState.NoResult)
    val subredditListingUiState: StateFlow<SubredditListingUiState> = _subredditListingUiState

    val searchResultUiState = userConfigRepository.userConfig
        .map {
            SearchResultUiState.Success(
                query = query,
                subredditScope = subredditScope,
                sortOption = sort,
                timeframe = timeframe,
                instance = if (it.shouldUseCustomInstance) it.customInstance else it.instance,
                blurNsfw = it.blurNsfw,
                blurSpoiler = it.blurSpoiler
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = SearchResultUiState.Loading
        )
        .apply {
            viewModelScope.launch {
                this@apply.collectLatest { state ->
                    if (state is SearchResultUiState.Success && query.isNotEmpty()) {
                        searchPostsAndSubreddits(query)
                    }
                }
            }
        }

    fun searchPostsAndSubreddits(query: String) {
        viewModelScope.launch {
            _postListUiState.value = PostListUiState.Loading

            val currentSearchResultUiState = searchResultUiState.value
            if (query.isNotBlank() && currentSearchResultUiState is SearchResultUiState.Success) {
                runCatching {
                    postRepository.searchPostsAndSubreddits(
                        instanceUrl = currentSearchResultUiState.instance,
                        query = query,
                        sort = currentSearchResultUiState.sortOption.uri,
                        timeframe = currentSearchResultUiState.timeframe.uri,
                        subredditName = currentSearchResultUiState.subredditScope
                    )
                }.onSuccess {
                    if (it.second.isNotEmpty()) {
                        _subredditListingUiState.value = SubredditListingUiState.Success
                    }

                    _postListUiState.value = PostListUiState.Success(
                        posts = it.first,
                        subreddits = it.second
                    )
                }.onFailure {
                    _postListUiState.value = PostListUiState.Error(
                        msg = it.message.toString()
                    )
                }
            } else {
                _postListUiState.value = PostListUiState.EmptyQuery
            }
        }
    }

    fun loadMoreSubreddits(query: String) {
        viewModelScope.launch {
            val currentPostListUiState = _postListUiState.value
            val currentSearchResultUiState = searchResultUiState.value

            if (currentPostListUiState is PostListUiState.Success &&
                currentSearchResultUiState is SearchResultUiState.Success) {
                _subredditListingUiState.value = SubredditListingUiState.Loading

                runCatching {
                    subredditRepository.searchSubreddits(currentSearchResultUiState.instance, query)
                }.onSuccess {
                    _subredditListingUiState.value = SubredditListingUiState.Success

                    _postListUiState.value = PostListUiState.Success(
                        posts = currentPostListUiState.posts,
                        subreddits = it
                    )
                }.onFailure {
                    _postListUiState.value = PostListUiState.Error(it.message.toString())
                }
            }
        }
    }

    @Synchronized
    fun loadMorePosts(
        query: String,
        sortOption: SortOption.Search,
        sortTimeframe: SortOption.Timeframe
    ) {
        viewModelScope.launch {
            val currentPostListUiState = _postListUiState.value
            val currentSearchResultUiState = searchResultUiState.value

            if (currentPostListUiState is PostListUiState.Success &&
                currentSearchResultUiState is SearchResultUiState.Success) {
                val posts = currentPostListUiState.posts.toMutableList()

                runCatching {
                    postRepository.searchPostsAndSubreddits(
                        instanceUrl = currentSearchResultUiState.instance,
                        query = query,
                        sort = sortOption.uri,
                        timeframe = sortTimeframe.uri,
                        after = posts.last().id,
                        subredditName = currentSearchResultUiState.subredditScope
                    )
                }.onSuccess {
                    posts.addAll(it.first)

                    _postListUiState.value = PostListUiState.Success(
                        posts = posts,
                        subreddits = currentPostListUiState.subreddits
                    )
                }
            }
        }
    }

    fun getPostLink(post: Post): String {
        val currentSearchResultUiState = searchResultUiState.value
        val instance = if (currentSearchResultUiState is SearchResultUiState.Success)
            currentSearchResultUiState.instance
        else
            ""

        return "${instance}/r/${post.subredditName}/comments/${post.id}"
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

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("subredditScope") subredditScope: String?,
            @Assisted("sort") sort: SortOption.Search,
            @Assisted("timeframe") timeframe: SortOption.Timeframe,
            @Assisted("query") query: String
        ): SearchResultViewModel
    }
}

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    data class Success(
        val query: String,
        val subredditScope: String?,
        val sortOption: SortOption.Search,
        val timeframe: SortOption.Timeframe,
        val instance: String,
        val blurNsfw: Boolean,
        val blurSpoiler: Boolean
    ) : SearchResultUiState
}

sealed interface PostListUiState {
    data object Loading : PostListUiState

    data object EmptyQuery : PostListUiState

    data class Error(
        val msg: String,
    ) : PostListUiState

    data class Success(
        val posts: List<Post>,
        val subreddits: List<Subreddit>
    ) : PostListUiState
}

sealed interface SubredditListingUiState {
    data object Loading : SubredditListingUiState

    data object NoResult : SubredditListingUiState

    data object Success : SubredditListingUiState
}