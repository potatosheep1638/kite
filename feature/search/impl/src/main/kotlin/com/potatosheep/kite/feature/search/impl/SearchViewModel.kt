package com.potatosheep.kite.feature.search.impl

import androidx.lifecycle.ViewModel
import com.potatosheep.kite.core.common.enums.SortOption
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted("subredditScope") subredditScope: String?,
    @Assisted("sort") sort: SortOption.Search,
    @Assisted("timeframe") timeframe: SortOption.Timeframe,
    @Assisted("query") query: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SearchUiState(
            query = query,
            subredditScope = subredditScope,
            initialSubredditScope = subredditScope,
            sortOption = sort,
            timeframe = timeframe
        )
    )
    val uiState: StateFlow<SearchUiState> = _uiState

    fun setUiState(
        query: String? = null,
        subredditScope: String? = null,
        sort: SortOption.Search? = null,
        timeframe: SortOption.Timeframe? = null
    ) {
        val currentUiState = _uiState.value
        _uiState.value = currentUiState.copy(
            query = query ?: currentUiState.query,
            subredditScope = subredditScope ?: currentUiState.subredditScope,
            sortOption = sort ?: currentUiState.sortOption,
            timeframe = timeframe ?: currentUiState.timeframe
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("subredditScope") subredditScope: String?,
            @Assisted("sort") sort: SortOption.Search,
            @Assisted("timeframe") timeframe: SortOption.Timeframe,
            @Assisted("query") query: String
        ): SearchViewModel
    }
}

data class SearchUiState(
    val query: String,
    val subredditScope: String?,
    val initialSubredditScope: String?,
    val sortOption: SortOption.Search,
    val timeframe: SortOption.Timeframe
)