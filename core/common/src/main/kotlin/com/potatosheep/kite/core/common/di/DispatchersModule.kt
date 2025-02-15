package com.potatosheep.kite.core.common.di

import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Dispatcher(KiteDispatchers.Default)
    @Provides
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Dispatcher(KiteDispatchers.IO)
    @Provides
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}