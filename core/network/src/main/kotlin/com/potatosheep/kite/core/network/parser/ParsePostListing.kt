package com.potatosheep.kite.core.network.parser

import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.network.model.NetworkMediaLink
import org.jsoup.nodes.Element

/*
 * Extraction methods specifically for a page displaying multiple posts
 * e.g., https://redlib.instance.com/r/subredditName
 */
internal fun extractPostListingTitle(element: Element): String {
    return extractPostTitleAndFlair(element)
        .select("a")
        .first { !it.hasClass("post_flair") }
        .text()
}

internal fun extractPostListingCommentCount(element: Element): Int {
    val commentCountString = element.select("a")
        .first { it.hasClass("post_comments") }
        .attr("title")
        .split(" ")[0]

    if (!commentCountString.contains(".")) {
        return commentCountString.toInt()
    } else {
        val commentCountNumbers =
            commentCountString
                .substringBefore("k")
                .split(".")

        return (commentCountNumbers[0] + commentCountNumbers[1] + "00").toInt()
    }
}

// Retrieves links for media (i.e., videos, images, news links, etc)
internal fun extractPostListingMediaLinks(
    element: Element,
    instanceUrl: String
): List<NetworkMediaLink> {
    val hasPostMediaContent =
        element.select("div").hasClass("post_media_content")
    val hasImageLink = element.select("a").hasClass("post_thumbnail")


    if (!hasPostMediaContent && hasImageLink) {
        val span = element.select("a")
            .first { it.hasClass("post_thumbnail") }
            .select("span")
            .first()

        val hasNoThumbnail = element.select("a")
            .hasClass("no_thumbnail")


        return if (span != null && span.text() == "gallery") {
            val thumbnail = element.select("a")
                .first { it.hasClass("post_thumbnail") }
                .select("image")
                .attr("href")

            val postLink = element.select("a")
                .first { it.hasClass("post_thumbnail") }
                .attr("href")

            listOf(
                NetworkMediaLink(
                    link =
                    if (!hasNoThumbnail)
                        instanceUrl + thumbnail
                    else
                        "",
                    caption = null,
                    mediaType = MediaType.GALLERY_THUMBNAIL
                ),
                NetworkMediaLink(
                    link = postLink,
                    caption = null,
                    mediaType = MediaType.GALLERY_LINK
                )
            )
        } else if (span != null) {
            val link = element.select("a")
                .first { it.hasClass("post_thumbnail") }
                .attr("href")

            val thumbnail = element.select("a")
                .first { it.hasClass("post_thumbnail") }
                .select("image")
                .attr("href")

            listOf(
                NetworkMediaLink(
                    link =
                    if (!hasNoThumbnail)
                        instanceUrl + thumbnail
                    else
                        "", // TODO: Remove this if article has no thumbnail
                    caption = null,
                    mediaType = MediaType.ARTICLE_THUMBNAIL
                ),
                NetworkMediaLink(
                    link =
                    when {
                        span.text() == "reddit.com" -> {
                            "https://www.reddit.com$link"
                        }

                        link.contains("/r/\\w+/comments/".toRegex()) -> {
                            //"$instanceUrl$link"
                            "https://www.reddit.com$link"
                        }

                        else -> link
                    },
                    caption = null,
                    mediaType = MediaType.ARTICLE_LINK
                )
            )
        } else {
            emptyList()
        }
    } else if (hasPostMediaContent &&
        element.select("div")
            .first { it.hasClass("post_media_content") }
            .select("a")
            .hasClass("post_media_image")
    ) {

        return listOf(
            if (element.select("img").first() == null) {
                NetworkMediaLink(
                    link = instanceUrl + element.select("image")
                        .first()
                        ?.attr("href"),
                    caption = null,
                    mediaType = MediaType.IMAGE
                )
            } else {
                NetworkMediaLink(
                    link = instanceUrl + element.select("img")
                        .first()
                        ?.attr("src"),
                    caption = null,
                    mediaType = MediaType.IMAGE
                )
            }
        )
    } else if (hasPostMediaContent &&
        element.select("video.post_media_video").first() != null
    ) {

        val videoPath = (element.select("source")
            .first()
            ?.attr("src")) ?: (element.select("video.post_media_video").first()?.attr("src"))

        return listOf(
            NetworkMediaLink(
                link = instanceUrl + (element.select("video").first()?.attr("poster") ?: ""),
                caption = null,
                mediaType = MediaType.VIDEO_THUMBNAIL
            ),
            NetworkMediaLink(
                link = instanceUrl + videoPath,
                caption = null,
                mediaType = MediaType.VIDEO
            )
        )
    } else {
        return emptyList()
    }
}