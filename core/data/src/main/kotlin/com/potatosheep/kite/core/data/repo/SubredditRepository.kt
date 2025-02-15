package com.potatosheep.kite.core.data.repo

import com.potatosheep.kite.core.data.model.toEntity
import com.potatosheep.kite.core.database.dao.SubredditDao
import com.potatosheep.kite.core.database.entity.toExternalModel
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.model.SubredditWiki
import com.potatosheep.kite.core.network.NetworkDataSource
import com.potatosheep.kite.core.network.model.toExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SubredditRepository {
    /*
     * While typically the retrieval of posts and a subreddit's About would be separate functions,
     * doing so here would result in two requests being made to the same page, as Redlib
     * (as of Aug 31, 2024) returns both a list of posts and the subreddit's About in the same page.
     *
     * Thus, an exception was made to prevent unnecessary requests.
     */
    // TODO: Review this if Redlib gets an API or changes how information is displayed.
    suspend fun getSubreddit(
        instanceUrl: String,
        subredditName: String
    ): Pair<Subreddit, List<Post>>

    suspend fun getSubredditWiki(
        instanceUrl: String,
        subredditName: String
    ): SubredditWiki

    fun getFollowedSubreddits(): Flow<List<Subreddit>>

    fun checkIfSubredditHasRecord(subredditName: String): Flow<Int>

    suspend fun setSubredditFollowed(subreddit: Subreddit, follow: Boolean)

    suspend fun searchSubreddits(
        instanceUrl: String,
        query: String
    ): List<Subreddit>
}

internal class DefaultSubredditRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val subredditDao: SubredditDao
) : SubredditRepository {

    override suspend fun getSubreddit(
        instanceUrl: String,
        subredditName: String
    ): Pair<Subreddit, List<Post>> {
        val subredditAndPosts = networkDataSource.getSubreddit(
            subredditName = subredditName,
            instanceUrl = instanceUrl
        )

        return Pair(
            subredditAndPosts.first.toExternalModel(),
            subredditAndPosts.second.map { it.toExternalModel() }
        )
    }

    override suspend fun getSubredditWiki(
        instanceUrl: String,
        subredditName: String
    ): SubredditWiki =
        networkDataSource.getSubredditWiki(
            subredditName = subredditName,
            instanceUrl = instanceUrl
        ).toExternalModel()

    // TODO: Move this to SearchRepository
    override suspend fun searchSubreddits(
        instanceUrl: String,
        query: String
    ): List<Subreddit> =
        networkDataSource.searchSubreddits(
            query = query,
            instanceUrl = instanceUrl
        ).map { it.toExternalModel() }

    override fun getFollowedSubreddits(): Flow<List<Subreddit>> =
        subredditDao.getAllSubreddits().map { list ->
            list.map { it.toExternalModel() }
        }

    override suspend fun setSubredditFollowed(subreddit: Subreddit, follow: Boolean) =
        if (follow)
            subredditDao.insertSubreddit(subreddit.toEntity())
        else
            subredditDao.deleteSubreddit(subreddit.toEntity())

    override fun checkIfSubredditHasRecord(subredditName: String): Flow<Int> =
        subredditDao.getSubredditCount(subredditName)
}