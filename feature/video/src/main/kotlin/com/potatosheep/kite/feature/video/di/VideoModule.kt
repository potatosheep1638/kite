package com.potatosheep.kite.feature.video.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// TODO: Move this to own module (:core:player)
@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    // TODO: Make this a factory instead
    @Provides
    @Singleton
    fun providePlayer(@ApplicationContext application: Context): Player =
            ExoPlayer.Builder(application).build()
}