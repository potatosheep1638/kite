package com.potatosheep.kite.feature.subreddit.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class SubredditNavKey(val subreddit: String) : NavKey

fun Navigator.navigateToSubreddit(subreddit: String) {
    navigate(SubredditNavKey(subreddit))
}