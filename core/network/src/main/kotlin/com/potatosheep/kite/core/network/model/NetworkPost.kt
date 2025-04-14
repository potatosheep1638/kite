package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.Post
import kotlinx.datetime.Instant

data class NetworkPost (
    val id: String,
    val title: String,
    val subRedditName: String,
    val userName: String,
    val mediaLinks: List<NetworkMediaLink>,
    val postTextContent: String,
    val postTimeAgo: String,
    val timePosted: Instant,
    val upvoteCount: Int,
    val postCommentCount: Int,
    val flair: List<NetworkFlairComponent>,
    val flairId: String,
    val isNsfw: Boolean,
    val isSpoiler: Boolean
)

fun NetworkPost.toExternalModel() = Post(
    id = id,
    title = title,
    subredditName = subRedditName,
    userName = userName,
    mediaLinks = mediaLinks.map { it.toExternalModel() },
    textContent = postTextContent,
    timeAgo = postTimeAgo,
    timePosted = timePosted,
    upvoteCount = upvoteCount,
    commentCount = postCommentCount,
    flair = flair.map { it.toExternalModel() },
    flairId = flairId,
    isNsfw = isNsfw,
    isSpoiler = isSpoiler
)