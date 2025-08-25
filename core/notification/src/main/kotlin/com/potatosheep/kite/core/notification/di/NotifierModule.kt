package com.potatosheep.kite.core.notification.di

import com.potatosheep.kite.core.notification.KiteNotifier
import com.potatosheep.kite.core.notification.Notifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface NotifierModule {
    @Binds
    fun binds(notifier: KiteNotifier): Notifier
}