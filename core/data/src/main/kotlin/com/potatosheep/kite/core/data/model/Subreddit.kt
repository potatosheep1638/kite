package com.potatosheep.kite.core.data.model

import com.potatosheep.kite.core.database.entity.SubredditEntity
import com.potatosheep.kite.core.model.Subreddit

fun Subreddit.toEntity() = SubredditEntity(
    subredditName = subredditName,
    iconLink = removeHost(iconLink),
    description = description,
)

private fun removeHost(link: String): String =
    if (link.isEmpty()) {
        ""
    } else {
        val linkPath = link.split("/")
        "/" + linkPath.subList(3, linkPath.size).joinToString("/")
    }