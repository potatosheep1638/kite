package com.potatosheep.kite.core.network.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/*
 * Extraction methods for a user page
 * e.g., https://redlib.instance.com/user/user123/
 */
internal fun extractUserName(html: Document): String =
    html.select("p").first { it.id() == "user_name" }.text()

internal fun extractUserDescription(html: Document): String =
    html.select("div").first { it.id() == "user_description" }.text()

internal fun extractUserCommentId(element: Element): String =
    element.select("a")
        .first { it.hasClass("comment_link") }
        .attr("href")
        .split("/")[6]

internal fun extractUserCommentPostId(element: Element): String =
    element.select("a")
        .first { it.hasClass("comment_link") }
        .attr("href")
        .split("/")[4]

internal fun extractUserCommentSubreddit(element: Element): String =
    element.select("a")
        .first { it.hasClass("comment_subreddit") }
        .attr("href")
        .substringAfter("r/")


internal fun extractUserCommentTextContent(element: Element, instanceUrl: String): String =
    element.select("div")
        .first { it.hasClass("md") }
        .html()
        .addDomain(instanceUrl)


internal fun extractUserPostCommentCount(element: Element): Int {
    val commentCountString = element.select("a")
        .first { it.hasClass("post_comments") }
        .text()
        .split(" ")[0]

    if (!commentCountString.contains(".")) {
        return commentCountString.toInt()
    }
    else {
        val commentCountNumbers =
            commentCountString
                .substringBefore("k")
                .split(".")

        return (commentCountNumbers[0] + commentCountNumbers[1] + "00").toInt()
    }
}

internal fun extractUserCommentPostTitle(element: Element): String =
    element.select("a").first { it.hasClass("comment_link") }.text()

internal fun extractUserKarma(html: Document): Int =
    html.select("div")
        .first { it.id() == "user_details" }
        .select("div")[1].text()
        .toInt()

internal fun extractUserIconLink(html: Document, instanceUrl: String): String =
    instanceUrl +
            html.select("img").first { it.id() == "user_icon" }.attr("src")
