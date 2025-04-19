package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.Comment

data class NetworkComment (
    val id: String,
    val postId: String,
    val userName: String,
    val subredditName: String,
    val textContent: String,
    val upvoteCount: Int,
    val timeAgo: String,
    val parentCommentId: String? = null,
    val postTitle: String? = null,
    val isPostAuthor: Boolean,
    val flair: List<NetworkFlairComponent>
)

fun NetworkComment.toExternalModel() = Comment(
    id = id,
    postId = postId,
    userName = userName,
    textContent = textContent,
    subredditName = subredditName,
    upvoteCount = upvoteCount,
    timeAgo = timeAgo,
    parentCommentId = parentCommentId,
    postTitle = postTitle,
    isPostAuthor = isPostAuthor,
    flair = flair.map { it.toExternalModel() }
)