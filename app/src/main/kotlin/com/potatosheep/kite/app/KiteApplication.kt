package com.potatosheep.kite.app

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.potatosheep.kite.core.markdown.renderer.MarkdownRenderer
import com.potatosheep.kite.core.markdown.renderer.MarkdownRendererFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KiteApplication : Application(), SingletonImageLoader.Factory, MarkdownRendererFactory {
    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    @Inject
    lateinit var markdownRenderer: dagger.Lazy<MarkdownRenderer>

    override fun newImageLoader(context: Context): ImageLoader = imageLoader.get()

    override fun newMarkdownRenderer(): MarkdownRenderer = markdownRenderer.get()
}