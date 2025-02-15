package com.potatosheep.kite.core.model

import kotlinx.datetime.Instant

data class Post(
    val id: String,
    val title: String,
    val subredditName: String,
    val userName: String,
    val mediaLinks: List<MediaLink>,
    val textContent: String,
    val timeAgo: String,
    val timePosted: Instant,
    val upvoteCount: Int,
    val commentCount: Int,
    val flair: List<FlairComponent>,
    val flairId: String,
    val isNsfw: Boolean,
    val isSpoiler: Boolean
)