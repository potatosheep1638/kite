package com.potatosheep.kite.core.data.di

import com.potatosheep.kite.core.data.repo.DefaultPostRepository
import com.potatosheep.kite.core.data.repo.DefaultSubredditRepository
import com.potatosheep.kite.core.data.repo.DefaultUserConfigRepository
import com.potatosheep.kite.core.data.repo.DefaultUserRepository
import com.potatosheep.kite.core.data.repo.PostRepository
import com.potatosheep.kite.core.data.repo.SubredditRepository
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.core.data.repo.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsPostRepository(
        postRepository: DefaultPostRepository
    ): PostRepository

    @Binds
    internal abstract fun bindsSubredditRepository(
        subredditRepository: DefaultSubredditRepository
    ): SubredditRepository

    @Binds
    internal abstract fun bindsUserConfigRepository(
        userConfigRepository: DefaultUserConfigRepository
    ): UserConfigRepository

    @Binds
    internal abstract fun bindsUserRepository(
        userRepository: DefaultUserRepository
    ): UserRepository
}