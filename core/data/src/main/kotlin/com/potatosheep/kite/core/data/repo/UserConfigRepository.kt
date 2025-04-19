package com.potatosheep.kite.core.data.repo

import com.potatosheep.kite.core.datastore.KitePreferencesDataSource
import com.potatosheep.kite.core.model.UserConfig
import com.potatosheep.kite.core.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserConfigRepository {
    val userConfig: Flow<UserConfig>
    suspend fun getInstanceCookies(instanceUrl: String)
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

    override suspend fun getInstanceCookies(instanceUrl: String) {
        networkDataSource.getPreferences(instanceUrl)
    }

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