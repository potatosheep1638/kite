@file:JvmName("ParseCommonKt")

package com.potatosheep.kite.core.network.parser

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/*
 * Common extraction methods that work on both post list pages and single post pages
 */
internal fun extractPostTitleAndFlair(element: Element): Element {
    return element.select("h2")
        .first { it.hasClass("post_title") }
}

internal fun extractPostHeader(element: Element): Element {
    return element.select("p")
        .first { it.hasClass("post_header") }
}

internal fun extractPostSubreddit(element: Element): String {
    return extractPostHeader(element).select("a")
        .first { it.hasClass("post_subreddit") }
        .text()
        .substringAfter("r/")
}

internal fun extractPostUser(element: Element): String {
    return extractPostHeader(element).select("a")
        .first { it.hasClass("post_author") }
        .text()
}

internal fun extractPostContent(element: Element, instanceUrl: String): String {
    val postContentElement =
        element.select("div")
            .first { it.hasClass("post_body") }
            .firstElementChild()
            ?.html()
            ?.addDomain(instanceUrl)

    return postContentElement ?: ""
}

internal fun extractPostTimeAgo(element: Element): String {
    return extractPostHeader(element).select("span")
        .first { it.hasClass("created") }
        .text()
}

internal fun extractPostUpvoteCount(element: Element): Int {
    val upvoteCountString = element.select("div")
        .first { it.hasClass("post_score") }
        .attr("title")
        .split(" ")[0]


    if (upvoteCountString.contains("Hidden")) {
        return -Int.MAX_VALUE
    } else if (!upvoteCountString.contains(".")) {
        return upvoteCountString.toInt()
    } else {
        val upvoteCountNumbers =
            upvoteCountString
                .substringBefore("k")
                .split(".")

        return (upvoteCountNumbers[0] + upvoteCountNumbers[1] + "00").toInt()
    }
}

internal fun extractPostNsfw(element: Element): Boolean =
    element.select("small.nsfw").first() != null

internal fun extractPostSpoiler(element: Element): Boolean =
    element.select("small.spoiler").first() != null

/*
 * Extraction methods for comments either in a post or from a user page
 * e.g.,
 *      https://redlib.instance.com/r/subredditName/comments/1dc4hux/...
 *  or
 *      https://redlib.instance.com/user/userName
 */
// TODO: Combine [extractCommentUpvoteCount] and [extractPostUpvoteCount]
internal fun extractCommentUpvoteCount(element: Element): Int {
    val upvoteCountString = element.select("p")
        .first { it.hasClass("comment_score") }
        .attr("title")
        .split(" ")[0]


    if (upvoteCountString.contains("Hidden")) {
        return -Int.MAX_VALUE
    } else if (!upvoteCountString.contains(".")) {
        return upvoteCountString.toInt()
    } else {
        val upvoteCountNumbers =
            upvoteCountString
                .substringBefore("k")
                .split(".")

        return (upvoteCountNumbers[0] + upvoteCountNumbers[1] + "00").toInt()
    }
}

/*
 * Extraction methods that work across screens.
 */
internal fun extractErrorMessage(html: Document, instanceUrl: String): String? {
    val errorDiv = html.select("div#error").first()

    return if (errorDiv == null) {
        null
    } else {
        "$instanceUrl returned:\n${errorDiv.select("h1").first()!!.text()}"
    }
}

internal fun extractTimestamp(element: Element): Instant {
    val timestampSpan = element.select("span.created").first()

    return if (timestampSpan == null) {
        Clock.System.now()
    } else {
        val timestamp = timestampSpan.attr("title")
            .replace("UTC", "")
            .trim()

        val customFormat = LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            char(' ')
            year()
            chars(", ")
            hour()
            char(':')
            minute()
            char(':')
            second()
        }

        val timestampToDateTime = LocalDateTime.parse(
            input = timestamp,
            format = customFormat
        )

        timestampToDateTime.toInstant(TimeZone.UTC)
    }
}

private enum class Month(
    val word: String,
    val numeric: String
) {
    JAN("Jan", "01"),
    FEB("Feb", "02"),
    MAR("Mar", "03"),
    APR("Apr", "04"),
    MAY("May", "05"),
    JUN("Jun", "06"),
    JUL("July", "07"),
    AUG("Aug", "08"),
    SEP("Sep", "09"),
    OCT("Oct", "10"),
    NOV("Nov", "11"),
    DEC("Dec", "12"),
}

/*
 * Helper methods
 */
internal fun String.addDomain(instanceUrl: String) =
    this.replace("href=\"/preview/", "href=\"$instanceUrl/preview/")
        .replace("src=\"/static/", "src=\"$instanceUrl/static/")
        .replace("href=\"/img/", "href=\"$instanceUrl/img/")
        .replace("src=\"/emote/", "src=\"$instanceUrl/emote/")
        .replace("href=\"/r/", "href=\"https://www.reddit.com/r/")
