package com.potatosheep.kite.core.domain

import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPostLinkUseCase @Inject constructor(
    private val userConfigRepository: UserConfigRepository
) {
    operator fun invoke(post: Post): Flow<String> =
        userConfigRepository.userConfig
            .map {
                "${it.instance}/r/${post.subredditName}/comments/${post.id}"
            }
}