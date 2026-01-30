package com.potatosheep.kite.feature.user.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.data.repo.UserRepository
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.User
import com.potatosheep.kite.feature.user.impl.navigation.UserNav
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
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = UserViewModel.Factory::class)
class UserViewModel @AssistedInject constructor(
    savedStateHandle: SavedStateHandle,
    userConfigRepository: UserConfigRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    @Assisted private val user: String
) : ViewModel() {

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userUiState: StateFlow<UserUiState> = _userUiState

    private val _userFeedUiState = MutableStateFlow<UserFeedUiState>(UserFeedUiState.Loading)
    val userFeedUiState: StateFlow<UserFeedUiState> = _userFeedUiState

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
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            runCatching {
                _userUiState.value = UserUiState.Loading
                userRepository.getUser(instanceUrl.first { it.isNotEmpty() }, user)
            }.onSuccess {
                _userUiState.value = UserUiState.Success(it.first)
                _userFeedUiState.value = UserFeedUiState.Success(it.second)
            }.onFailure {
                _userUiState.value = UserUiState.Error(
                    msg = it.message.toString()
                )
            }
        }
    }

    fun loadSortedPostsAndComments(sort: SortOption.User) {
        viewModelScope.launch {
            _userFeedUiState.value = UserFeedUiState.Loading

            val posts = userRepository.getUserPostAndComments(
                instanceUrl = instanceUrl.value,
                userName = user,
                sort = sort.uri
            )

            _userFeedUiState.value = UserFeedUiState.Success(posts)
        }
    }

    fun loadMorePostsAndComments(sort: SortOption.User) {
        viewModelScope.launch {
            if (_userFeedUiState.value is UserFeedUiState.Success) {
                val postsAndComments = (_userFeedUiState.value as UserFeedUiState.Success)
                    .postAndComments
                    .toMutableList()

                val after =
                    when {
                        postsAndComments.last() is Post -> {
                            val post = (postsAndComments.last() as Post).id
                            "t3_${post}"
                        }

                        postsAndComments.last() is Comment -> {
                            val comment = (postsAndComments.last() as Comment).id
                            "t1_${comment}"
                        }

                        else -> null
                    }

                val newPostsAndComments = userRepository.getUserPostAndComments(
                    instanceUrl = instanceUrl.value,
                    userName = user,
                    sort = sort.uri,
                    after = after
                )

                postsAndComments.addAll(newPostsAndComments)

                _userFeedUiState.value = UserFeedUiState.Success(postsAndComments)
            }
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

    @AssistedFactory
    interface Factory {
        fun create(user: String): UserViewModel
    }
}

// TODO: Add failure state
sealed interface UserUiState {
    data object Loading : UserUiState

    data class Success(
        val user: User
    ) : UserUiState

    data class Error(
        val msg: String
    ) : UserUiState
}

// TODO: Add failure state
sealed interface UserFeedUiState {
    data object Loading : UserFeedUiState

    data class Success(
        val postAndComments: List<Any>
    ) : UserFeedUiState
}
