package com.potatosheep.kite.core.database.di

import android.content.Context
import androidx.room.Room
import com.potatosheep.kite.core.database.FlairComponentsConverter
import com.potatosheep.kite.core.database.KiteDatabase
import com.potatosheep.kite.core.database.MediaLinksConverter
import com.potatosheep.kite.core.database.dao.PostDao
import com.potatosheep.kite.core.database.dao.SubredditDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesKiteDatabase(
        @ApplicationContext context: Context,
        flairComponentsConverter: FlairComponentsConverter,
        mediaLinksConverter: MediaLinksConverter
    ): KiteDatabase = Room.databaseBuilder(
        context = context,
        klass = KiteDatabase::class.java,
        name = "kite-database"
    )
        .addTypeConverter(flairComponentsConverter)
        .addTypeConverter(mediaLinksConverter)
        .build()

    @Provides
    fun providesFlairComponentsConverter(
        moshi: dagger.Lazy<Moshi>
    ): FlairComponentsConverter = FlairComponentsConverter(
        moshi.get()
    )

    @Provides
    fun providesMediaLinksConverter(
        moshi: dagger.Lazy<Moshi>
    ): MediaLinksConverter = MediaLinksConverter(
        moshi
    )

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun providesPostDao(
        database: KiteDatabase
    ): PostDao = database.postDao()

    @Provides
    fun providesSubredditDao(
        database: KiteDatabase
    ): SubredditDao = database.subredditDao()
}