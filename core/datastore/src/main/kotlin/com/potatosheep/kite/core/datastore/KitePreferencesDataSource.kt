package com.potatosheep.kite.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.potatosheep.kite.core.model.UserConfig
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class KitePreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userConfig = userPreferences.data
        .map {
            UserConfig(
                instance = it.instance,
                shouldHideOnboarding = it.shouldHideOnboarding,
                showNsfw = it.showNsfw,
                blurNsfw = it.blurNsfw,
                shouldUseCustomInstance = it.shouldUseCustomInstance,
                customInstance = it.customInstance,
                blurSpoiler = it.blurSpoiler
            )
        }

    suspend fun setInstance(instance: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.instance = instance
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setOnboarding(show: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.shouldHideOnboarding = !show
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setBlurNsfw(shouldBlur: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.blurNsfw = shouldBlur
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setShowNsfw(shouldShow: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.showNsfw = shouldShow
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setUseCustomInstance(shouldUse: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.shouldUseCustomInstance = shouldUse
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setCustomInstance(instance: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.customInstance = instance
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setBlurSpoiler(shouldBlur: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.showNsfw = shouldBlur
                }
            }
        } catch (e: IOException) {
            Log.e("KitePreferences", "Failed to update user preferences", e)
        }
    }
}