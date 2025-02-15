package com.potatosheep.kite.core.network.parser

import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.network.model.NetworkMediaLink
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/*
 * Extraction methods specifically for a single post
 * e.g., https://redlib.instance.com/r/subredditName/comments/1dc4hux/...
 */
internal fun extractPostId(html: Document): String {
    return html
        .select("meta")
        .first { element ->
            element.hasAttr("property") &&
                    element.attr("property") == "og:url"
        }
        .attr("content")
        .split("/")[4]
}

internal fun extractPostTitle(html: Document): String =
    html.select("h1.post_title")
        .first()!!
        .ownText()

internal fun extractPostCommentCount(html: Document): Int {
    val commentCountString = html.select("p")
        .first { it.attr("id") == "comment_count" }
        .text()
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

internal fun extractPostMediaLinks(html: Document, instanceUrl: String): List<NetworkMediaLink> {
    val hasPostMediaContent =
        html.select("div").hasClass("post_media_content")
    val hasGallery = html.select("div").hasClass("gallery")
    val hasLink = html.select("a").any { node ->
        node.attr("id") == "post_url"
    }

    return if (hasPostMediaContent &&
        html.select("div")
            .first { it.hasClass("post_media_content") }
            .select("a")
            .hasClass("post_media_image")
    ) {
        listOf(
            if (html.select("img").first() == null) {
                NetworkMediaLink(
                    link = instanceUrl + html.select("image")
                        .first()
                        ?.attr("href"),
                    caption = null,
                    mediaType = MediaType.IMAGE
                )
            } else {
                NetworkMediaLink(
                    link = instanceUrl + html.select("img")
                        .first()
                        ?.attr("src"),
                    caption = null,
                    mediaType = MediaType.IMAGE
                )
            }
        )
    } else if (hasGallery) {
        extractPostImageGallery(html, instanceUrl)
    } else if (hasLink) {
        val link = html.select("a")
            .first { it.attr("id") == "post_url" }
            .attr("href")

        listOf(
            NetworkMediaLink(
                link = "",
                caption = null,
                mediaType = MediaType.ARTICLE_THUMBNAIL
            ),
            NetworkMediaLink(
                link =
                when {
                    link.matches("/gallery/[a-zA-Z0-9]{7}".toRegex()) -> {
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
    } else if (hasPostMediaContent &&
        html.select("video.post_media_video").first() != null
    ) {
        val thumbnailLink = (html.select("video").first()?.attr("poster") ?: "")

        val videoLink: String? =
                html.select("source")
                    .first()
                    ?.attr("src") ?:
                    html.select("video.post_media_video")
                        .first()
                        ?.attr("src")

        listOf(
            NetworkMediaLink(
                link = instanceUrl + thumbnailLink,
                caption = null,
                mediaType = MediaType.VIDEO_THUMBNAIL
            ),
            NetworkMediaLink(
                link = instanceUrl + videoLink,
                caption = null,
                mediaType = MediaType.VIDEO
            )
        )
    } else {
        emptyList()
    }
}

internal fun extractPostImageGallery(
    element: Element,
    instanceUrl: String
): List<NetworkMediaLink> {
    val galleryElement = element.select("div")
        .first { it.hasClass("gallery") }

    val imageLinksList = emptyList<NetworkMediaLink>().toMutableList()

    galleryElement.forEachNode { node ->
        if (node is Element && node.nameIs("figure")) {
            val imageLink =
                instanceUrl +
                node.select("a").first()?.attr("href")

            val caption =
                if (node.select("figcaption").first() != null)
                    node.text()
                else
                    null

            imageLinksList += NetworkMediaLink(
                link = imageLink,
                caption = caption,
                mediaType = MediaType.GALLERY_IMAGE
            )
        }
    }

    return imageLinksList
}