package com.potatosheep.kite.core.media.di

import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import com.potatosheep.kite.core.media.KiteDownloadService
import com.potatosheep.kite.core.media.MediaDownloadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.Call
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object MediaModule {

    @Provides
    @Singleton
    fun downloadService(
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @Dispatcher(KiteDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MediaDownloadService = KiteDownloadService(okHttpCallFactory, ioDispatcher)
}