package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.Subreddit

data class NetworkSubreddit(
    val subredditName: String,
    val subscribers: Int,
    val activeUsers: Int,
    val iconLink: String,
    val summary: String,
    val sidebar: String
)

fun NetworkSubreddit.toExternalModel(): Subreddit = Subreddit(
    subredditName = subredditName,
    subscribers = subscribers,
    activeUsers = activeUsers,
    iconLink = iconLink,
    description = summary,
    sidebar = sidebar
)