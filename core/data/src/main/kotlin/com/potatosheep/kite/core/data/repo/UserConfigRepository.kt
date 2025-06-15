package com.potatosheep.kite.core.data.repo

import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.datastore.KitePreferencesDataSource
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.UserConfig
import com.potatosheep.kite.core.network.NetworkDataSource
import com.potatosheep.kite.core.network.model.toExternalModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserConfigRepository {
    val userConfig: Flow<UserConfig>
    /**
     * Redlib (as of Jun 12, 2025) returns the HTML of the front page after they use a restore link.
     * Thus, to prevent having to make an additional, redundant request to retrieve posts (via
     * [PostRepository.getPost]) whenever a settings change occurs, this method returns a [List] of
     * [Post] objects.
     *
     * If Redlib ever gets an API, this method will likely be changed or even outright removed.
     */
    suspend fun getInstanceCookies(
        instanceUrl: String,
        sort: String = SortOption.Post.HOT.uri,
        subreddits: List<String> = emptyList(),
        redirect: String = ""
        ): List<Post>
    suspend fun setInstance(instanceUrl: String)
    suspend fun setNsfwBlur(shouldBlur: Boolean)
    suspend fun setOnboarding(shouldOnboard: Boolean)
    suspend fun setUseCustomInstance(shouldUse: Boolean)
    suspend fun setCustomInstance(instanceUrl: String)
    suspend fun setBlurSpoiler(shouldBlur: Boolean)
    suspend fun getInstances(): List<String>
}

// TODO: Implement datastore
internal class DefaultUserConfigRepository @Inject constructor(
    private val kitePreferencesDataSource: KitePreferencesDataSource,
    private val networkDataSource: NetworkDataSource,
) : UserConfigRepository {

    override val userConfig = kitePreferencesDataSource.userConfig

    override suspend fun getInstanceCookies(
        instanceUrl: String,
        sort: String,
        subreddits: List<String>,
        redirect: String
    ): List<Post> = networkDataSource.getPreferences(
        instanceUrl = instanceUrl,
        sort = sort,
        subreddits = subreddits
    ).map { it.toExternalModel() }

    override suspend fun setInstance(instanceUrl: String) {
        kitePreferencesDataSource.setInstance(instanceUrl)
    }

    override suspend fun setNsfwBlur(shouldBlur: Boolean) {
        kitePreferencesDataSource.setBlurNsfw(shouldBlur)
    }

    override suspend fun setOnboarding(shouldOnboard: Boolean) {
        kitePreferencesDataSource.setOnboarding(shouldOnboard)
    }

    override suspend fun setUseCustomInstance(shouldUse: Boolean) {
        kitePreferencesDataSource.setUseCustomInstance(shouldUse)
    }

    override suspend fun setCustomInstance(instanceUrl: String) {
        kitePreferencesDataSource.setCustomInstance(instanceUrl)
    }

    override suspend fun setBlurSpoiler(shouldBlur: Boolean) {
        kitePreferencesDataSource.setBlurSpoiler(shouldBlur)
    }

    override suspend fun getInstances(): List<String> =
        networkDataSource.getInstances()
}