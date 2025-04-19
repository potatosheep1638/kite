package com.potatosheep.kite.core.markdown.di

import android.content.Context
import com.potatosheep.kite.core.markdown.converter.MarkdownConverter
import com.potatosheep.kite.core.markdown.renderer.MarkdownRenderer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object MarkdownModule {

    @Provides
    @Singleton
    fun markdownRenderer(
        @ApplicationContext application: Context
    ): MarkdownRenderer = MarkdownRenderer(application)

    @Provides
    @Singleton
    fun markdownConverter(): MarkdownConverter = MarkdownConverter()
}