package com.potatosheep.kite.core.network.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/*
 * Extraction methods for subreddit pages
 */
internal fun extractSubredditName(html: Document): String =
    html.select("p#sub_name").first()!!.text().substringAfter("r/")

internal fun extractSubredditMembers(html: Document): Int =
    html.select("div").first { it.id() == "sub_meta" }
        .select("div").first { it.hasAttr("title") }
        .attr("title")
        .toInt()

internal fun extractSubredditActiveUsers(html: Document): Int =
    html.select("div").first { it.id() == "sub_meta" }
        .select("div").last { it.hasAttr("title") }
        .attr("title")
        .toInt()

internal fun extractSubredditIconLink(html: Document, instanceUrl: String): String {
    val link = html.select("img").first { it.id() == "sub_icon" }
        .attr("src")

    return if (link.isBlank()) {
        link
    } else {
        "${instanceUrl}${link}"
    }
}

internal fun extractSubredditWiki(html: Document): String =
    html.select("div").first { it.id() == "wiki" }.html()

internal fun extractSubredditDescription(html: Document): String =
    html.select("div").first { it.id() == "sub_meta" }
        .select("p").first { it.id() == "sub_description" }
        .html()

internal fun extractSubredditSidebar(html: Document): String =
    html.select("div").first { it.id() == "sidebar_contents" }.html()


/*
 * Extraction methods for subreddit entries in search pages
 */
internal fun extractSearchSubredditName(element: Element): String =
    element.select("span")
        .first { it.hasClass("search_subreddit_name") }
        .text()
        .substringAfter("r/")

internal fun extractSearchSubredditMembers(element: Element): Int =
    element.select("span").first { it.hasClass("search_subreddit_members") }
        .attr("title")
        .split("Members")[0]
        .trim()
        .toInt()

internal fun extractSearchSubredditIconLink(element: Element, instanceUrl: String): String {
    val linkElement = element.select("img").first()

    return if (linkElement == null) {
        ""
    } else {
        "${instanceUrl}${linkElement.attr("src")}"
    }
}

internal fun extractSearchSubredditDescription(element: Element): String =
    element.select("p").first { it.hasClass("search_subreddit_description") }.text()