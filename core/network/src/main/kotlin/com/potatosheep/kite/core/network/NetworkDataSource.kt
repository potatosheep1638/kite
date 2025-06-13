package com.potatosheep.kite.core.network

import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.network.model.NetworkComment
import com.potatosheep.kite.core.network.model.NetworkMediaLink
import com.potatosheep.kite.core.network.model.NetworkPost
import com.potatosheep.kite.core.network.model.NetworkSubreddit
import com.potatosheep.kite.core.network.model.NetworkSubredditWiki
import com.potatosheep.kite.core.network.model.NetworkUser

interface NetworkDataSource {
    /**
     * Redlib (as of Jun 12, 2025) redirects users to the front page after they use a restore link.
     * Thus, to prevent having to make an additional, redundant request to retrieve posts (via
     * [getPost]) whenever a settings change occurs, this method returns a [List] of [NetworkPost]
     * objects.
     *
     * If Redlib ever gets an API, this method will likely be changed or even outright removed.
     */
    suspend fun getPreferences(
        instanceUrl: String,
        sort: String = SortOption.Post.HOT.uri,
        subreddits: List<String> = emptyList(),
    ): List<NetworkPost>

    suspend fun getPost(
        instanceUrl: String,
        subredditName: String,
        postId: String,
        replyId: String? = null,
        isShareLink: Boolean = false,
        findParentComments: Boolean = false
    ): Triple<NetworkPost, List<NetworkComment>, String>

    suspend fun getPosts(
        instanceUrl: String,
        sort: String = SortOption.Post.HOT.uri,
        timeframe: String = SortOption.Timeframe.DAY.uri,
        after: String? = null,
        subredditName: String? = null
    ): List<NetworkPost>

    suspend fun searchPostsAndSubreddits(
        instanceUrl: String,
        query: String,
        sort: String = SortOption.Search.RELEVANCE.uri,
        timeframe: String = SortOption.Timeframe.ALL.uri,
        after: String? = null,
        subredditName: String? = null
    ): Pair<List<NetworkPost>, List<NetworkSubreddit>>

    suspend fun searchSubreddits(
        instanceUrl: String,
        query: String
    ): List<NetworkSubreddit>

    suspend fun getPostImageGallery(
        instanceUrl: String,
        subredditName: String,
        postId: String
    ): List<NetworkMediaLink>

    /*
     * While typically the retrieval of posts and a subreddit's About would be separate functions,
     * doing so here would result in two requests being made to the same page, as Redlib
     * (as of Aug 31, 2024) returns both in the same page.
     *
     * Thus, an exception was made to prevent unnecessary requests.
     */
    // TODO: Review this if Redlib gets an API or changes how information is displayed.
    suspend fun getSubreddit(
        subredditName: String,
        instanceUrl: String
    ): Pair<NetworkSubreddit, List<NetworkPost>>

    suspend fun getSubredditWiki(subredditName: String, instanceUrl: String): NetworkSubredditWiki

    /*
     * While typically the retrieval of posts, comments and a user's details would be separate
     * functions, doing so here would result in multiple requests being made to the same page, as
     * Redlib (as of Aug 31, 2024) returns all three in the same page.
     *
     * Thus, an exception was made to prevent unnecessary requests.
     *
     * Note that List<Any> contains both NetworkPost and NetworkComment objects
     */
    // TODO: Review this if Redlib gets an API or changes how information is displayed.
    suspend fun getUser(userName: String, instanceUrl: String): Pair<NetworkUser, List<Any>>

    suspend fun getUserPostAndComments(
        instanceUrl: String,
        userName: String,
        sort: String = SortOption.User.HOT.uri,
        after: String? = null
    ): List<Any>

    suspend fun getInstances(): List<String>
}