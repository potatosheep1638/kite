package com.potatosheep.kite.core.model

data class Comment(
    val id: String,
    val postId: String,
    val userName: String,
    val subredditName: String,
    val textContent: String,
    val upvoteCount: Int,
    val timeAgo: String,
    val parentCommentId: String?,
    val postTitle: String?,
    val isPostAuthor: Boolean,
)