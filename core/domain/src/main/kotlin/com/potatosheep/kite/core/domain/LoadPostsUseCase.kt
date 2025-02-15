package com.potatosheep.kite.core.domain

import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.model.Post
import javax.inject.Inject

class LoadPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(
        instanceUrl: String,
        sort: String = SortOption.Post.HOT.uri,
        timeframe: String = SortOption.Timeframe.DAY.uri,
        after: String? = null,
        subredditName: String? = null,
        existingPosts: List<Post> = emptyList()
    ) {
        TODO()
    }
}