package com.potatosheep.kite.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.potatosheep.kite.core.data.repo.SubredditRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Subreddit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    userConfigRepository: UserConfigRepository,
    private val subredditRepository: SubredditRepository
) : ViewModel() {

    val subredditListUiState = combine(
        userConfigRepository.userConfig.map { it.instance },
        subredditRepository.getFollowedSubreddits(),
    ) { instance, subreddits ->
        SubredditListUiState.Success(
            subreddits.map { subreddit ->
                subreddit.copy(
                    iconLink = "$instance${subreddit.iconLink}"
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = SubredditListUiState.Loading
    )

    fun removeSubreddit(subreddit: Subreddit) {
        viewModelScope.launch {
            subredditRepository.setSubredditFollowed(subreddit, false)
        }
    }
}

sealed interface SubredditListUiState {

    data object Loading : SubredditListUiState

    data class Success(
        val subreddits: List<Subreddit>
    ) : SubredditListUiState
}