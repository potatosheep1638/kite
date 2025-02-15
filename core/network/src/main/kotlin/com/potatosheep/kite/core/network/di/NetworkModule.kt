package com.potatosheep.kite.core.network.di

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil3.ImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.potatosheep.kite.core.markdown.converter.MarkdownConverter
import com.potatosheep.kite.core.network.NetworkDataSource
import com.potatosheep.kite.core.network.SimpleCookieJar
import com.potatosheep.kite.core.network.client.ParserNetwork
import com.potatosheep.kite.core.network.parser.Parser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {
    @Binds
    fun binds(networkService: ParserNetwork): NetworkDataSource

    companion object {

        @Provides
        @Singleton
        fun okHttpCallFactory(@ApplicationContext application: Context): Call.Factory =
            OkHttpClient.Builder()
                .cache(
                    Cache(
                        directory = application.cacheDir,
                        maxSize = (10 * 1024 * 1024).toLong()
                    )
                )
                .cookieJar(SimpleCookieJar(emptyList()))
                .addNetworkInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .header(
                            name = "Cache-Control",
                            value = CacheControl.Builder()
                                .maxAge(1, TimeUnit.MINUTES)
                                .build()
                                .toString()
                        )
                        .build()

                    chain.proceed(request)
                }
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()

        @Provides
        @Singleton
        fun parser(
            markdownConverter: dagger.Lazy<MarkdownConverter>
        ): Parser = Parser(markdownConverter.get())

        @Provides
        @Singleton
        fun imageLoader(
            okHttpCallFactory: dagger.Lazy<Call.Factory>,
            @ApplicationContext application: Context
        ): ImageLoader = ImageLoader.Builder(application)
            .crossfade(true)
            .components {
                add(OkHttpNetworkFetcherFactory(
                    callFactory = {
                        okHttpCallFactory.get()
                    }
                ))

                if (SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory()  )
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}