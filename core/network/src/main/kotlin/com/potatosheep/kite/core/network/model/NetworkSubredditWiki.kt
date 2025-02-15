package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.SubredditWiki

data class NetworkSubredditWiki(
    val content: String
)

fun NetworkSubredditWiki.toExternalModel() = SubredditWiki(
    content = content
)