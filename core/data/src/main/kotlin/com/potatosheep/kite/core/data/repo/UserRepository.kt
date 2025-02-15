package com.potatosheep.kite.core.data.repo

import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.model.User
import com.potatosheep.kite.core.network.NetworkDataSource
import com.potatosheep.kite.core.network.model.NetworkComment
import com.potatosheep.kite.core.network.model.NetworkPost
import com.potatosheep.kite.core.network.model.toExternalModel
import javax.inject.Inject

interface UserRepository {
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
    suspend fun getUser(instanceUrl: String, userName: String): Pair<User, List<Any>>
    suspend fun getUserPostAndComments(
        instanceUrl: String,
        userName: String,
        sort: String = SortOption.User.HOT.uri,
        after: String? = null
    ): List<Any>
}

internal class DefaultUserRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) : UserRepository {

    override suspend fun getUser(
        instanceUrl: String,
        userName: String
    ): Pair<User, List<Any>> {
        val response = networkDataSource.getUser(
            userName = userName,
            instanceUrl = instanceUrl,
        )

        val userMeta = response.first.toExternalModel()
        val userPostsAndComments = response.second.map {
            when (it) {
                is NetworkPost -> {
                    it.toExternalModel()
                }
                is NetworkComment -> {
                    it.toExternalModel()
                }
                else -> Unit
            }
        }

        return Pair(userMeta, userPostsAndComments)
    }

    override suspend fun getUserPostAndComments(
        instanceUrl: String,
        userName: String,
        sort: String,
        after: String?
    ): List<Any> {
        val response = networkDataSource.getUserPostAndComments(
            instanceUrl = instanceUrl,
            userName = userName,
            sort = sort,
            after = after
        )

        val userPostsAndComments = response.map {
            when (it) {
                is NetworkPost -> {
                    it.toExternalModel()
                }
                is NetworkComment -> {
                    it.toExternalModel()
                }
                else -> Unit
            }
        }

        return userPostsAndComments
    }
}