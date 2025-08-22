package com.potatosheep.kite.core.media.di

import androidx.lifecycle.LifecycleService
import com.potatosheep.kite.core.common.KiteServices
import com.potatosheep.kite.core.common.Service
import com.potatosheep.kite.core.media.KiteDownloadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object MediaModule {

    @Service(KiteServices.Download)
    @Provides
    @Singleton
    fun downloadService(): LifecycleService = KiteDownloadService()
}