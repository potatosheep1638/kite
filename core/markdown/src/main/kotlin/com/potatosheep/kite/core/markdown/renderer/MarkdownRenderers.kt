package com.potatosheep.kite.core.markdown.renderer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.potatosheep.kite.core.markdown.plugin.CoilImagesPlugin
import com.potatosheep.kite.core.markdown.plugin.spoiler.SpoilerPlugin
import com.potatosheep.kite.core.markdown.plugin.superscript.SuperscriptPlugin
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TableAwareMovementMethod
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlEmptyTagReplacement
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.movement.MovementMethodPlugin

fun MarkdownRenderer(context: Context): MarkwonRenderer = MarkwonRenderer(
    Markwon.builder(context)
        .usePlugins(
            listOf(
                SpoilerPlugin.create(),
                SuperscriptPlugin.create(),
                StrikethroughPlugin.create(),
                TablePlugin.create(context),
                HtmlPlugin.create().emptyTagReplacement(HtmlEmptyTagReplacement()),
                CoilImagesPlugin.create(
                    object : CoilImagesPlugin.CoilStore {
                        override fun load(drawable: AsyncDrawable): ImageRequest {
                            return ImageRequest.Builder(context)
                                .defaults(context.imageLoader.defaults)
                                .data(drawable.destination)
                                .placeholder(
                                    (com.potatosheep.kite.core.designsystem.R.drawable.image)
                                )
                                .listener { _, result ->
                                    if (result.image.asDrawable(context.resources) is Animatable) {
                                        (result.image.asDrawable(context.resources) as Animatable).start()
                                    }
                                }
                                .build()
                        }

                        override fun cancel(disposable: Disposable) {
                            disposable.dispose()
                        }
                    },
                    context.imageLoader,
                ),
                LinkResolverPlugin(),
                MovementMethodPlugin.create(TableAwareMovementMethod.create())
            )
        )
        .build()
)

class LinkResolverPlugin : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.linkResolver { view, link ->
            val uri = when {
                link.contains("giphy") -> {
                    val giphyGifId = link.substringAfterLast("/")
                    val giphyLink = "https://media.giphy.com/media/$giphyGifId/giphy.gif"

                    "kite://kite-app/image/?imageLinks=${Uri.encode(giphyLink)}".toUri()
                }

                link.contains("(\\.jpg|\\.jpeg|\\.png|\\.gif)".toRegex()) -> {
                    "kite://kite-app/image/?imageLinks=${Uri.encode(link)}".toUri()
                }

                else -> {
                    link.toUri()
                }
            }

            val intent = Intent(Intent.ACTION_VIEW, uri)

            try {
                view.context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.w(
                    "CustomLinkResolver",
                    "Activity was not found for the link: '$link'"
                )
            }
        }
    }
}