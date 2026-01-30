package com.potatosheep.kite.feature.post.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class PostNavKey(
    val subreddit: String,
    val postId: String,
    val commentId: String?,
    val thumbnailLink: String?,
    val isShareLink: Boolean,
    val findParents: Boolean,
) : NavKey

fun Navigator.navigateToPost(
    subreddit: String,
    postId: String,
    commentId: String?,
    thumbnailLink: String? = null,
    isShareLink: Boolean = false,
    findParents: Boolean = false,
) {
    navigate(PostNavKey(
        subreddit = subreddit,
        postId = postId,
        commentId = commentId,
        thumbnailLink = thumbnailLink,
        isShareLink = isShareLink,
        findParents = findParents
    ))
}