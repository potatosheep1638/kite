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
import kotlinx.coroutines.flow.first
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
    val sortOption = savedStateHandle.getStateFlow(SORT_OPTION, sort)
    val timeframe = savedStateHandle.getStateFlow(TIMEFRAME, timeframe)
    val subredditScope = savedStateHandle.getStateFlow(SUBREDDIT_SCOPE, subredditScope)
    val query = savedStateHandle.getStateFlow(QUERY, query)

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState

    private val _subredditListingUiState =
        MutableStateFlow<SubredditListingUiState>(SubredditListingUiState.NoResult)
    val subredditListingUiState: StateFlow<SubredditListingUiState> = _subredditListingUiState

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
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                searchPostsAndSubreddits(query)
            }
        }
    }

    fun searchPostsAndSubreddits(query: String) {
        viewModelScope.launch {
            newQuery(query)
            _searchUiState.value = SearchUiState.Loading

            if (query.isNotBlank()) {
                runCatching {
                    postRepository.searchPostsAndSubreddits(
                        instanceUrl = instanceUrl.value
                            .ifBlank {
                                instanceUrl.first {
                                    it.isNotBlank()
                                }
                            },
                        query = query,
                        sort = savedStateHandle.get<SortOption.Search>(SORT_OPTION)!!.uri,
                        timeframe = savedStateHandle.get<SortOption.Timeframe>(TIMEFRAME)!!.uri,
                        subredditName = savedStateHandle[SUBREDDIT_SCOPE]
                    )
                }.onSuccess {
                    if (it.second.isNotEmpty()) {
                        _subredditListingUiState.value = SubredditListingUiState.Success
                    }

                    _searchUiState.value = SearchUiState.Success(
                        posts = it.first,
                        subreddits = it.second
                    )
                }.onFailure {
                    _searchUiState.value = SearchUiState.Error(
                        msg = it.message.toString()
                    )
                }
            } else {
                _searchUiState.value = SearchUiState.EmptyQuery
            }
        }
    }

    fun loadMoreSubreddits(query: String) {
        viewModelScope.launch {
            newQuery(query)

            if (_searchUiState.value is SearchUiState.Success) {
                _subredditListingUiState.value = SubredditListingUiState.Loading

                val posts = (_searchUiState.value as SearchUiState.Success).posts

                runCatching {
                    subredditRepository.searchSubreddits(instanceUrl.value, query)
                }.onSuccess {
                    _subredditListingUiState.value = SubredditListingUiState.Success

                    _searchUiState.value = SearchUiState.Success(
                        posts = posts,
                        subreddits = it
                    )
                }.onFailure {
                    _searchUiState.value = SearchUiState.Error(it.message.toString())
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
            newQuery(query)

            if (_searchUiState.value is SearchUiState.Success) {
                val posts = (_searchUiState.value as SearchUiState.Success).posts.toMutableList()
                val subreddits = (_searchUiState.value as SearchUiState.Success).subreddits

                runCatching {
                    postRepository.searchPostsAndSubreddits(
                        instanceUrl = instanceUrl.value,
                        query = query,
                        sort = sortOption.uri,
                        timeframe = sortTimeframe.uri,
                        after = posts.last().id,
                        subredditName = savedStateHandle[SUBREDDIT_SCOPE]
                    )
                }.onSuccess {
                    posts.addAll(it.first)

                    _searchUiState.value = SearchUiState.Success(
                        posts = posts,
                        subreddits = subreddits
                    )
                }
            }
        }
    }

    fun changeSubredditScope(subredditName: String?) {
        savedStateHandle[SUBREDDIT_SCOPE] = subredditName
    }

    fun changeSortOption(
        sort: SortOption.Search? = null,
        timeframe: SortOption.Timeframe? = null
    ) {
        if (sort != null) savedStateHandle[SORT_OPTION] = sort
        if (timeframe != null) savedStateHandle[TIMEFRAME] = timeframe
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

    fun checkIfValidUrl(query: String): Boolean {
        return when {
            query.matches(POST_PATTERN.toRegex()) -> {
                newQuery(query)
                true
            }

            query.matches(POST_SHARE_PATTERN.toRegex()) -> {
                newQuery(query)
                true
            }

            else -> {
                false
            }
        }
    }

    private fun newQuery(query: String) {
        savedStateHandle[QUERY] = query
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("subredditScope") subredditScope: String?,
            @Assisted("sort") sort: SortOption.Search,
            @Assisted("timeframe") timeframe: SortOption.Timeframe,
            @Assisted("query") query: String
        ) : SearchResultViewModel
    }
}

sealed interface SearchUiState {
    data object Initial : SearchUiState

    data object Loading : SearchUiState

    data object EmptyQuery : SearchUiState

    data class Error(
        val msg: String,
    ) : SearchUiState

    data class Success(
        val posts: List<Post>,
        val subreddits: List<Subreddit>
    ) : SearchUiState
}

sealed interface SubredditListingUiState {
    data object Loading : SubredditListingUiState

    data object NoResult : SubredditListingUiState

    data object Success : SubredditListingUiState
}

private const val SORT_OPTION = "sortOption"
private const val TIMEFRAME = "timeframe"
private const val SUBREDDIT_SCOPE = "subredditScope"
private const val QUERY = "query"

private const val POST_PATTERN = "https://.*/r/.*/comments/.*/?(.*/.*)?"
private const val POST_SHARE_PATTERN = "https://.*/r/.*/s/.*/?(.*/.*)?"
