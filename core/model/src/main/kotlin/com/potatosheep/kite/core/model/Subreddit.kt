package com.potatosheep.kite.core.model

data class Subreddit (
    val subredditName: String,
    val subscribers: Int,
    val activeUsers: Int,
    val iconLink: String,
    val description: String,
    val sidebar: String
)